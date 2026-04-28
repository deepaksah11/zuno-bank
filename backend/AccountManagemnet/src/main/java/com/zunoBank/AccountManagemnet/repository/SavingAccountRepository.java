package com.zunoBank.AccountManagemnet.repository;


import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingAccountRepository
        extends JpaRepository<SavingAccount, Long> {

    @EntityGraph(attributePaths = {"customer"})
    Optional<SavingAccount> findByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = {"customer"})
    Optional<SavingAccount> findByCif(String cif);

    @EntityGraph(attributePaths = {"customer"})
    Optional<SavingAccount> findByCustomerAndStatus(
            Customer customer, AccountStatus status);

    // ✅ returns List not Optional
    @EntityGraph(attributePaths = {"customer"})
    List<SavingAccount> findByStatusAndBranchCode(
            AccountStatus status, String branchCode);

    boolean existsByCustomer(Customer customer);

    List<SavingAccount> findByBranchCode(String branchCode);
    Optional<SavingAccount> findByCustomer(Customer customer);
}