package com.zunoBank.AccountManagemnet.service;


import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;

    // ── Debit ─────────────────────────────────────────────────────────────
    @Transactional
    public void debit(String accountNumber, BigDecimal amount) {

        // try saving account first
        var sa = savingAccountRepository
                .findByAccountNumber(accountNumber);

        if (sa.isPresent()) {
            SavingAccount account = sa.get();

            if (account.getStatus() != AccountStatus.ACTIVE)
                throw new AccountException(
                        "Account is " + account.getStatus()
                                + ". Transactions not allowed.");

            if (account.getBalance().subtract(amount)
                    .compareTo(account.getMinimumBalance()) < 0)
                throw new AccountException(
                        "Insufficient balance. Min balance ₹"
                                + account.getMinimumBalance()
                                + " must be maintained.");

            account.setBalance(account.getBalance().subtract(amount));
            account.setLastTransactionAt(LocalDateTime.now());
            savingAccountRepository.save(account);
            return;
        }

        // try current account
        var ca = currentAccountRepository
                .findByAccountNumber(accountNumber);

        if (ca.isPresent()) {
            CurrentAccount account = ca.get();

            if (account.getStatus() != AccountStatus.ACTIVE)
                throw new AccountException(
                        "Account is " + account.getStatus()
                                + ". Transactions not allowed.");

            if (account.getBalance().subtract(amount)
                    .compareTo(account.getMinimumBalance()) < 0)
                throw new AccountException(
                        "Insufficient balance. Min balance ₹"
                                + account.getMinimumBalance()
                                + " must be maintained.");

            account.setBalance(account.getBalance().subtract(amount));
            account.setLastTransactionAt(LocalDateTime.now());
            currentAccountRepository.save(account);
            return;
        }

        throw new com.zunoBank.AccountManagemnet.service.AccountException(
                "Account not found: " + accountNumber);
    }

    // ── Credit ────────────────────────────────────────────────────────────
    @Transactional
    public void credit(String accountNumber, BigDecimal amount) {

        // ✅ try saving account by account number
        var sa = savingAccountRepository
                .findByAccountNumber(accountNumber);

        if (sa.isPresent()) {
            SavingAccount account = sa.get();

            if (account.getStatus() == AccountStatus.CLOSED)
                throw new AccountException(
                        "Cannot credit to a closed account");

            account.setBalance(account.getBalance().add(amount));
            account.setLastTransactionAt(LocalDateTime.now());

            if (account.getStatus() == AccountStatus.DORMANT)
                account.setStatus(AccountStatus.ACTIVE);

            savingAccountRepository.save(account);
            return;
        }

        // ✅ try current account by account number
        var ca = currentAccountRepository
                .findByAccountNumber(accountNumber);  // ← must be findByAccountNumber

        if (ca.isPresent()) {
            CurrentAccount account = ca.get();

            if (account.getStatus() == AccountStatus.CLOSED)
                throw new AccountException(
                        "Cannot credit to a closed account");

            account.setBalance(account.getBalance().add(amount));
            account.setLastTransactionAt(LocalDateTime.now());

            if (account.getStatus() == AccountStatus.DORMANT)
                account.setStatus(AccountStatus.ACTIVE);

            currentAccountRepository.save(account);
            return;
        }

        throw new AccountException(
                "Account not found: " + accountNumber);
    }
}
