package com.zunoBank.AccountManagemnet.repository;


import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// CustomerRepository.java
@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCifAndBranchCode(String cif, String branchCode);

    List<Customer> findByStatusAndBranchCode(
            CustomerStatus status, String branchCode);

    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    boolean existsByPanNumber(String panNumber);

    List<Customer> findByBranchCode(String branchCode);
    Page<Customer> findByBranchCode(String branchCode, Pageable pageable);

    Optional<Customer> findByCif(String existingCif);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}