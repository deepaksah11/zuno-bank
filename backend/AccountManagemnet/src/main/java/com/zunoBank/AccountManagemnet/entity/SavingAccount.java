package com.zunoBank.AccountManagemnet.entity;

import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "saving_accounts")
@Data
@NoArgsConstructor
public class SavingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;          // NULL until manager approves

    private String cif;                    // copied from customer after approval

    // ── Relationship ──────────────────────────────────────────────────────
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id",
            nullable = false,
            unique = true)
    private Customer customer;

    // ── Account Details ───────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(precision = 15, scale = 2)
    private BigDecimal initialDeposit;

    // savings specific fields
    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;       // e.g. 3.5%

    @Column(precision = 15, scale = 2)
    private BigDecimal minimumBalance;     // default 500

    private String ifscCode;
    private String branchCode;
    private String branchName;

    // ── RO ────────────────────────────────────────────────────────────────
    private String createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;

    // ── Manager ───────────────────────────────────────────────────────────
    private String approvedByManagerId;
    private String approvedByManagerName;
    private LocalDateTime actionTakenAt;
    private String rejectionReason;

    // ── Timestamps ────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime closedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.submittedAt = LocalDateTime.now();
        this.status = AccountStatus.PENDING_APPROVAL;
        this.balance = BigDecimal.ZERO;
        this.minimumBalance = new BigDecimal("500");
        this.interestRate = new BigDecimal("3.50");
        // accountNumber = null until manager approves
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // called when manager approves
    public void generateAccountNumber() {
        this.accountNumber = "SB"
                + this.branchCode
                + String.format("%05d",
                (int)(Math.random() * 90000) + 10000);
        this.status = AccountStatus.ACTIVE;
        this.balance = this.initialDeposit;
        this.lastTransactionAt = LocalDateTime.now();
    }
}
