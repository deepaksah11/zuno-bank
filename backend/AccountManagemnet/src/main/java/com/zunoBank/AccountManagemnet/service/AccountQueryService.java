package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.dto.CurrentAccountDTO;
import com.zunoBank.AccountManagemnet.dto.CustomerAccountsDTO;
import com.zunoBank.AccountManagemnet.dto.OnboardingResponseDTO;
import com.zunoBank.AccountManagemnet.dto.SavingAccountDTO;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.error.AccountException;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.CustomerRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountQueryService {
    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;

    public OnboardingResponseDTO getByCif(String cif) {

        Customer customer = customerRepository
                .findByCif(cif)
                .orElseThrow(() -> new AccountException(
                        "Customer not found with CIF: " + cif));

        Optional<SavingAccount> sa =
                savingAccountRepository.findByCif(cif);

        Optional<CurrentAccount> ca =
                currentAccountRepository.findByCif(cif);

        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .accountId(sa.map(SavingAccount::getId).orElse(
                        ca.map(CurrentAccount::getId).orElse(null)))
                .accountNumber(
                        sa.map(SavingAccount::getAccountNumber).orElse(
                                ca.map(CurrentAccount::getAccountNumber)
                                        .orElse(null)))
                .accountType(sa.isPresent()
                        ? AccountType.SAVINGS
                        : ca.isPresent()
                        ? AccountType.CURRENT : null)
                .balance(sa.map(SavingAccount::getBalance).orElse(
                        ca.map(CurrentAccount::getBalance).orElse(null)))
                .initialDeposit(
                        sa.map(SavingAccount::getInitialDeposit).orElse(
                                ca.map(CurrentAccount::getInitialDeposit)
                                        .orElse(null)))
                .interestRate(
                        sa.map(SavingAccount::getInterestRate)
                                .orElse(null))
                .overdraftLimit(
                        ca.map(CurrentAccount::getOverdraftLimit)
                                .orElse(null))
                .minimumBalance(
                        sa.map(SavingAccount::getMinimumBalance).orElse(
                                ca.map(CurrentAccount::getMinimumBalance)
                                        .orElse(null)))
                .branchCode(
                        sa.map(SavingAccount::getBranchCode).orElse(
                                ca.map(CurrentAccount::getBranchCode)
                                        .orElse(null)))
                .ifscCode(sa.map(SavingAccount::getIfscCode).orElse(
                        ca.map(CurrentAccount::getIfscCode)
                                .orElse(null)))
                .branchName(
                        sa.map(SavingAccount::getBranchName).orElse(
                                ca.map(CurrentAccount::getBranchName)
                                        .orElse(null)))
                .approvedByManagerId(customer.getApprovedByManagerId())
                .approvedByManagerName(
                        customer.getApprovedByManagerName())
                .actionTakenAt(customer.getActionTakenAt())
                .createdByRoId(customer.getCreatedByRoId())
                .roName(customer.getRoName())
                .submittedAt(customer.getSubmittedAt())
                .build();
    }

    public OnboardingResponseDTO getSavingAccount(
            String accountNumber) {

        SavingAccount sa = savingAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(
                        "Saving account not found: " + accountNumber));

        Customer customer = sa.getCustomer();

        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .accountId(sa.getId())
                .accountNumber(sa.getAccountNumber())
                .accountType(AccountType.SAVINGS)
                .balance(sa.getBalance())
                .initialDeposit(sa.getInitialDeposit())
                .interestRate(sa.getInterestRate())
                .minimumBalance(sa.getMinimumBalance())
                .ifscCode(sa.getIfscCode())
                .branchCode(sa.getBranchCode())
                .branchName(sa.getBranchName())
                .createdByRoId(sa.getCreatedByRoId())
                .roName(sa.getRoName())
                .submittedAt(sa.getSubmittedAt())
                .approvedByManagerId(sa.getApprovedByManagerId())
                .approvedByManagerName(sa.getApprovedByManagerName())
                .actionTakenAt(sa.getActionTakenAt())
                .build();
    }

    public OnboardingResponseDTO getCurrentAccount(
            String accountNumber) {

        CurrentAccount ca = currentAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(
                        "Current account not found: " + accountNumber));

        Customer customer = ca.getCustomer();

        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .accountId(ca.getId())
                .accountNumber(ca.getAccountNumber())
                .accountType(AccountType.CURRENT)
                .balance(ca.getBalance())
                .initialDeposit(ca.getInitialDeposit())
                .overdraftLimit(ca.getOverdraftLimit())
                .minimumBalance(ca.getMinimumBalance())
                .ifscCode(ca.getIfscCode())
                .branchCode(ca.getBranchCode())
                .branchName(ca.getBranchName())
                .createdByRoId(ca.getCreatedByRoId())
                .roName(ca.getRoName())
                .submittedAt(ca.getSubmittedAt())
                .approvedByManagerId(ca.getApprovedByManagerId())
                .approvedByManagerName(ca.getApprovedByManagerName())
                .actionTakenAt(ca.getActionTakenAt())
                .build();
    }

    public CustomerAccountsDTO getAllAccountsByCif(String cif) {

        Customer customer = customerRepository
                .findByCif(cif)
                .orElseThrow(() -> new AccountException(
                        "Customer not found with CIF: " + cif));

        Optional<SavingAccount> sa =
                savingAccountRepository.findByCif(cif);

        Optional<CurrentAccount> ca =
                currentAccountRepository.findByCif(cif);

        return CustomerAccountsDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .pincode(customer.getPincode())
                .savingAccount(sa.map(s -> SavingAccountDTO.builder()
                        .id(s.getId())
                        .accountNumber(s.getAccountNumber())
                        .status(s.getStatus())
                        .balance(s.getBalance())
                        .initialDeposit(s.getInitialDeposit())
                        .interestRate(s.getInterestRate())
                        .minimumBalance(s.getMinimumBalance())
                        .ifscCode(s.getIfscCode())
                        .branchCode(s.getBranchCode())
                        .branchName(s.getBranchName())
                        .createdAt(s.getCreatedAt())
                        .lastTransactionAt(s.getLastTransactionAt())
                        .build()).orElse(null))
                .currentAccount(ca.map(c -> CurrentAccountDTO.builder()
                        .id(c.getId())
                        .accountNumber(c.getAccountNumber())
                        .status(c.getStatus())
                        .balance(c.getBalance())
                        .initialDeposit(c.getInitialDeposit())
                        .overdraftLimit(c.getOverdraftLimit())
                        .minimumBalance(c.getMinimumBalance())
                        .ifscCode(c.getIfscCode())
                        .branchCode(c.getBranchCode())
                        .branchName(c.getBranchName())
                        .createdAt(c.getCreatedAt())
                        .lastTransactionAt(c.getLastTransactionAt())
                        .build()).orElse(null))
                .build();
    }
}
