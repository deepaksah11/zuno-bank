package com.zunoBank.Transactions.dto;

import com.zunoBank.Transactions.entity.type.SpendingCategory;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.entity.type.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private String referenceId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private SpendingCategory category;
    private String description;
    private String failureReason;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}
