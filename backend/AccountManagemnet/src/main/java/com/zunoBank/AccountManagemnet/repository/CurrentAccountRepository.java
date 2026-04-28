package com.zunoBank.AccountManagemnet.repository;


import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentAccountRepository
        extends JpaRepository<CurrentAccount, Long> {

    @EntityGraph(attributePaths = {"customer"})
    Optional<CurrentAccount> findByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = {"customer"})
    Optional<CurrentAccount> findByCif(String cif);

    @EntityGraph(attributePaths = {"customer"})
    Optional<CurrentAccount> findByCustomerAndStatus(
            Customer customer, AccountStatus status);

    // ✅ returns List not Optional
    @EntityGraph(attributePaths = {"customer"})
    List<CurrentAccount> findByStatusAndBranchCode(
            AccountStatus status, String branchCode);

    boolean existsByCustomer(Customer customer);

    List<CurrentAccount> findByBranchCode(String branchCode);

    Optional<CurrentAccount> findByCustomer(Customer customer);
}