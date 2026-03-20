package com.zunoBank.AccountManagemnet.dto;


import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CurrentAccountDTO {
    private Long id;
    private String accountNumber;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal initialDeposit;
    private BigDecimal overdraftLimit;
    private BigDecimal minimumBalance;
    private String ifscCode;
    private String branchCode;
    private String branchName;
    private LocalDateTime createdAt;
    private LocalDateTime lastTransactionAt;
}
