package com.zunoBank.Transactions.entity;

import com.zunoBank.Transactions.entity.type.SpendingCategory;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.entity.type.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceId;

    private String senderAccountNumber;
    private String senderCif;

    private String receiverAccountNumber;
    private String receiverIfsc;
    private String receiverName;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    private SpendingCategory category;

    private String description;
    private String failureReason;

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist(){
        this.initiatedAt = LocalDateTime.now();
        this.referenceId = "TXN"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
        if (this.status == null) {
            this.status = TransactionStatus.PENDING;
        }
    }

}
