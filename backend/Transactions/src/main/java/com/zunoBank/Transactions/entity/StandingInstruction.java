package com.zunoBank.Transactions.entity;

import com.zunoBank.Transactions.entity.type.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "standing_instructions")
@Data
@NoArgsConstructor
public class StandingInstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerCif;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String receiverIfsc;
    private String receiverName;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String frequency;             // DAILY, WEEKLY, MONTHLY
    private boolean active;

    private LocalDateTime nextExecutionAt;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }
}
