package com.zunoBank.AccountManagemnet.entity;

import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
public class Account {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String accountNumber;


        private Long cif;

        @Enumerated(EnumType.STRING)
        private AccountType accountType;

        @Enumerated(EnumType.STRING)
        private AccountStatus status;

        private BigDecimal balance;

        private String ifscCode;
        private String branchName;
        private String branchCode;

        private BigDecimal initialDeposit;


        private Long createdByRoId;
        private String roName;
        private LocalDateTime submittedAt;

        private Long approvedByManagerId;
        private String managerName;
        private LocalDateTime actionTakenAt;
        private String rejectionReason;

        private boolean kycVerified;

        private LocalDateTime createdAt;
        private LocalDateTime lastTransactionAt;
        private LocalDateTime closedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.submittedAt = LocalDateTime.now();
        this.status = AccountStatus.PENDING_APPROVAL;
        this.balance = BigDecimal.ZERO;
        this.kycVerified = false;
    }

        public void generateAccountNumber() {
            this.accountNumber = "AC"
                    + this.branchCode
                    + System.currentTimeMillis();
        }
}
