package com.zunoBank.Transactions.repository;

import com.zunoBank.Transactions.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByCustomerCifAndActive(String customerCif, boolean active);

    Optional<Beneficiary> findByCustomerCifAndAccountNumber(String customerCif, String accountNumber);

    boolean existsByCustomerCifAndAccountNumber(String customerCif, String accountNumber);
}