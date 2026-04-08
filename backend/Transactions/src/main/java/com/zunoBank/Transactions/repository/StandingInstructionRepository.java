package com.zunoBank.Transactions.repository;

import com.zunoBank.Transactions.entity.StandingInstruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface StandingInstructionRepository extends JpaRepository<StandingInstruction, Long> {
    List<StandingInstruction> findByCustomerCif(String customerCif);

    List<StandingInstruction> findByActiveAndNextExecutionAtBefore(boolean active, LocalDateTime now);
}