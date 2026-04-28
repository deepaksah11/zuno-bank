package com.zunoBank.Transactions.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequestDTO {

    @NotNull
    private String accountNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;
}
