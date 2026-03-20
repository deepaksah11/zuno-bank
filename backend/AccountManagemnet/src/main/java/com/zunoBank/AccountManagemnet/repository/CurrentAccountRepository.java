package com.zunoBank.AccountManagemnet.entity;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CurrentAccountRepository.java
@Repository
public interface CurrentAccountRepository
        extends JpaRepository<CurrentAccount, Long> {

    Optional<CurrentAccount> findByAccountNumber(String accountNumber);
    Optional<CurrentAccount> findByCustomerId(Long customerId);
    Optional<CurrentAccount> findByCif(String cif);
}