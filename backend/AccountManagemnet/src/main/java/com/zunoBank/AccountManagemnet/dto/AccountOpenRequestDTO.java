package com.zunoBank.AccountManagemnet.dto;


import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class AccountOpenRequestDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;           // SAVINGS or CURRENT

    @NotNull(message = "Initial deposit is required")
    @Positive(message = "Initial deposit must be positive")
    private BigDecimal initialDeposit;

    @NotNull(message = "Branch name is required")
    private String branchName;

    @NotNull(message = "Branch code is required")
    private String branchCode;

    @NotNull(message = "IFSC code is required")
    private String ifscCode;

    @NotNull(message = "RO ID is required")
    private Long createdByRoId;

    private String roName;
}
