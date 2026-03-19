package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.config.NotificationFeignClient;
import com.zunoBank.AccountManagemnet.config.TransactionFeignClient;
import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.entity.Account;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.error.AccountException;
import com.zunoBank.AccountManagemnet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionFeignClient transactionFeignClient;
    private final NotificationFeignClient notificationFeignClient;
    private final AccountNumberGeneratorService accountNumberGenerator;

    @Value("${account.min-balance-savings}")
    private BigDecimal minBalanceSavings;

    @Value("${account.min-balance-current}")
    private BigDecimal minBalanceCurrent;

    // ── STEP 1: RO creates account request ───────────────────────────────

    @Override
    public AccountResponseDTO createAccountRequest(AccountOpenRequestDTO request) {

        // block duplicate account type per customer
        if (accountRepository.existsByCifAndAccountType(
                request.getCustomerId(), request.getAccountType())) {
            throw new AccountException(
                    "Customer already has a " + request.getAccountType() + " account");
        }

        // validate minimum initial deposit
        BigDecimal minDeposit = request.getAccountType() == AccountType.SAVINGS
                ? minBalanceSavings : minBalanceCurrent;

        if (request.getInitialDeposit().compareTo(minDeposit) < 0) {
            throw new AccountException(
                    "Minimum initial deposit for "
                            + request.getAccountType() + " account is ₹" + minDeposit);
        }

        Account account = new Account();
        account.setCif(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setInitialDeposit(request.getInitialDeposit());
        account.setBranchName(request.getBranchName());
        account.setBranchCode(request.getBranchCode());
        account.setIfscCode(request.getIfscCode());
        account.setCreatedByRoId(request.getCreatedByRoId());
        account.setRoName(request.getRoName());
        // status = PENDING_APPROVAL set via @PrePersist

        Account saved = accountRepository.save(account);

        // notify branch manager
        try {
            notificationFeignClient.notifyManager(
                    request.getBranchCode(),
                    "New " + request.getAccountType()
                            + " account request for Customer #"
                            + request.getCustomerId() + " is pending approval.");
        } catch (Exception e) {
            // notification failure should not break the flow
            System.err.println("Notification failed: " + e.getMessage());
        }

        return mapToResponse(saved);
    }

    // ── STEP 2: Manager approves or rejects ───────────────────────────────

    @Override
    public AccountResponseDTO processApproval(AccountApprovalDTO approval) {

        Account account = accountRepository.findById(approval.getAccountId())
                .orElseThrow(() -> new AccountException(
                        "Account request not found: #" + approval.getAccountId()));

        if (account.getStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new AccountException(
                    "Account is not pending approval. Current status: "
                            + account.getStatus());
        }

        account.setApprovedByManagerId(approval.getManagerId());
        account.setManagerName(approval.getManagerName());
        account.setActionTakenAt(LocalDateTime.now());

        if (approval.isApproved()) {

            // generate structured account number
            String accountNumber = accountNumberGenerator.generate(
                    account.getBranchCode(),
                    account.getAccountType()
            );


            account.setAccountNumber(accountNumber);
            account.setStatus(AccountStatus.ACTIVE);
            account.setBalance(account.getInitialDeposit());
            account.setLastTransactionAt(LocalDateTime.now());
            account.setKycVerified(true);
            try {
                notificationFeignClient.notifyCustomer(
                        account.getCif(),
                        "Your " + account.getAccountType()
                                + " account is approved! "
                                + "Account Number: " + accountNumber
                );
            } catch (Exception e) {
                System.out.println("[WARN] Notification skipped: " + e.getMessage());
            }
        }
 else {

            // ── REJECT ────────────────────────────────────────────────────
            if (approval.getRejectionReason() == null
                    || approval.getRejectionReason().isBlank()) {
                throw new AccountException(
                        "Rejection reason is mandatory when rejecting an account");
            }

            account.setStatus(AccountStatus.REJECTED);
            account.setRejectionReason(approval.getRejectionReason());

            try {
                notificationFeignClient.notifyCustomer(
                        account.getCif(),
                        "Your account request has been rejected. Reason: "
                                + approval.getRejectionReason()
                                + ". Please visit the branch for more details.");
            } catch (Exception e) {
                System.err.println("Notification failed: " + e.getMessage());
            }
        }

        return mapToResponse(accountRepository.save(account));
    }

    // ── Manager views pending queue ───────────────────────────────────────

    @Override
    public List<PendingAccountDTO> getPendingAccounts(String branchCode) {
        return accountRepository
                .findByStatusAndBranchCode(AccountStatus.PENDING_APPROVAL, branchCode)
                .stream()
                .map(this::mapToPending)
                .collect(Collectors.toList());
    }

    // ── Get account details ───────────────────────────────────────────────

    @Override
    public AccountResponseDTO getAccountDetails(String accountNumber) {
        return mapToResponse(findAccount(accountNumber));
    }

    // ── Get all accounts for a customer ──────────────────────────────────

    @Override
    public List<AccountResponseDTO> getAccountsByCustomer(Long customerId) {
        return accountRepository.findByCif(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Full paginated statement ──────────────────────────────────────────

    @Override
    public Page<MiniStatementDTO> getStatement(
            String accountNumber, int page, int size) {
        findAccount(accountNumber);  // validate exists
        return transactionFeignClient.getFullStatement(accountNumber, page, size);
    }

    // ── Mini statement — last 5 transactions ─────────────────────────────

    @Override
    public List<MiniStatementDTO> getMiniStatement(String accountNumber) {
        findAccount(accountNumber);  // validate exists
        return transactionFeignClient.getMiniStatement(accountNumber);
    }

    // ── Freeze account ────────────────────────────────────────────────────

    @Override
    public void freezeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED)
            throw new AccountException("Cannot freeze a closed account");
        if (account.getStatus() == AccountStatus.FROZEN)
            throw new AccountException("Account is already frozen");
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
    }

    // ── Unfreeze account ──────────────────────────────────────────────────

    @Override
    public void unfreezeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        if (account.getStatus() != AccountStatus.FROZEN)
            throw new AccountException("Account is not frozen");
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    // ── Close account (soft delete) ───────────────────────────────────────

    @Override
    public void closeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);

        if (account.getStatus() == AccountStatus.CLOSED)
            throw new AccountException("Account is already closed");

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0)
            throw new AccountException(
                    "Cannot close account. Remaining balance: ₹"
                            + account.getBalance()
                            + ". Please withdraw before closing.");

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    // ── Debit — called by transaction-service via Feign ───────────────────

    @Override
    @Transactional
    public void debit(String accountNumber, BigDecimal amount) {
        Account account = findAccount(accountNumber);

        if (account.getStatus() != AccountStatus.ACTIVE)
            throw new AccountException(
                    "Account is " + account.getStatus()
                            + ". Debit not allowed.");

        BigDecimal minBalance = account.getAccountType() == AccountType.SAVINGS
                ? minBalanceSavings : minBalanceCurrent;

        BigDecimal balanceAfterDebit = account.getBalance().subtract(amount);

        if (balanceAfterDebit.compareTo(minBalance) < 0)
            throw new AccountException(
                    "Insufficient balance. Minimum balance of ₹"
                            + minBalance + " must be maintained. "
                            + "Available for transfer: ₹"
                            + account.getBalance().subtract(minBalance));

        account.setBalance(balanceAfterDebit);
        account.setLastTransactionAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    // ── Credit — called by transaction-service via Feign ──────────────────

    @Override
    @Transactional
    public void credit(String accountNumber, BigDecimal amount) {
        Account account = findAccount(accountNumber);

        if (account.getStatus() == AccountStatus.CLOSED)
            throw new AccountException("Cannot credit to a closed account");

        account.setBalance(account.getBalance().add(amount));
        account.setLastTransactionAt(LocalDateTime.now());

        // auto-reactivate dormant account on incoming credit
        if (account.getStatus() == AccountStatus.DORMANT) {
            account.setStatus(AccountStatus.ACTIVE);
            System.out.println("Account " + accountNumber
                    + " reactivated from DORMANT to ACTIVE");
        }

        accountRepository.save(account);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber (accountNumber)
                .orElseThrow(() -> new AccountException(
                        "Account not found: " + accountNumber));
    }

    private AccountResponseDTO mapToResponse(Account a) {
        return AccountResponseDTO.builder()
                .id(a.getId())
                .accountNumber(a.getAccountNumber())
                .customerId(a.getCif())
                .accountType(a.getAccountType())
                .status(a.getStatus())
                .balance(a.getBalance())
                .branchName(a.getBranchName())
                .branchCode(a.getBranchCode())
                .ifscCode(a.getIfscCode())
                .kycVerified(a.isKycVerified())
                .createdByRoId(a.getCreatedByRoId())
                .roName(a.getRoName())
                .approvedByManagerId(a.getApprovedByManagerId())
                .managerName(a.getManagerName())
                .rejectionReason(a.getRejectionReason())
                .submittedAt(a.getSubmittedAt())
                .actionTakenAt(a.getActionTakenAt())
                .createdAt(a.getCreatedAt())
                .lastTransactionAt(a.getLastTransactionAt())
                .build();
    }

    private PendingAccountDTO mapToPending(Account a) {
        return PendingAccountDTO.builder()
                .accountId(a.getId())
                .customerId(a.getCif())
                .accountType(a.getAccountType())
                .initialDeposit(a.getInitialDeposit())
                .branchName(a.getBranchName())
                .branchCode(a.getBranchCode())
                .createdByRoId(a.getCreatedByRoId())
                .roName(a.getRoName())
                .submittedAt(a.getSubmittedAt())
                .build();
    }
}
