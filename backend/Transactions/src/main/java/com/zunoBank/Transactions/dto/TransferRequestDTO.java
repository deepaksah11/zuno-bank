package com.zunoBank.Transactions.dto;

import com.zunoBank.Transactions.entity.type.SpendingCategory;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.entity.type.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferRequestDTO {
    @NotNull(message = "Sender account is required")
    private String senderAccountNumber;

    @NotNull(message = "Sender CIF is required")
    private String senderCif;

    @NotNull(message = "Receiver account is required")
    private String receiverAccountNumber;

    @NotNull(message = "Receiver IFSC is required")
    private String receiverIfsc;

    private String receiverName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    private String description;
}
