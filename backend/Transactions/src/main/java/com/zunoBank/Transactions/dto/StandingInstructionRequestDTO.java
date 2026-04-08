package com.zunoBank.Transactions.dto;

import com.zunoBank.Transactions.entity.type.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StandingInstructionRequestDTO {
    @NotNull
    private String receiverAccountNumber;

    @NotNull
    private String receiverIfsc;

    private String receiverName;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private String frequency;             // DAILY, WEEKLY, MONTHLY

    @NotNull
    private LocalDateTime firstExecutionAt;

    private String cif;

    private String senderAccountNumber;
}
