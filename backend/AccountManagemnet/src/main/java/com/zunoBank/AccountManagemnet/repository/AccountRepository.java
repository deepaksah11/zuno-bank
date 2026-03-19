package com.zunoBank.AccountManagemnet.repository;

import com.zunoBank.AccountManagemnet.entity.Account;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
//    public List<Account> findByStatusAndLastTransactionAtBefore(AccountStatus accountStatus, LocalDateTime cutoff) {
//    }


    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByCifAndAccountType(@NotNull(message = "Customer ID is required") Long customerId, AccountType accountType);

    List<Account> findByStatusAndBranchCode(AccountStatus accountStatus, String branchCode);

    List<Account> findByCif(Long customerId);

    List<Account> findByStatusAndLastTransactionAtBefore(AccountStatus accountStatus, LocalDateTime cutoff);
}
