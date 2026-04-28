package com.zunoBank.Transactions.repository;

import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.entity.type.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.senderAccountNumber = :account " +
            "AND t.status = 'SUCCESS' " +
            "AND t.initiatedAt >= :startOfDay ")
    BigDecimal sumTodayTransactions(
            @Param("account") String account,
            @Param("startOfDay") LocalDateTime startOfDay);

    Page<Transaction> findBySenderAccountNumberAndTypeAndInitiatedAtBetween(
            String accountNumber,
            TransactionType type,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<Transaction> findBySenderAccountNumber(
            String accountNumber, Pageable pageable);
    List<Transaction> findTop5BySenderAccountNumberOrderByInitiatedAtDesc(
            String accountNumber);

    long countByInitiatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.type = 'DEPOSIT'
    AND t.completedAt BETWEEN :start AND :end
""")
    BigDecimal sumDepositsBetween(LocalDateTime start, LocalDateTime end);
}