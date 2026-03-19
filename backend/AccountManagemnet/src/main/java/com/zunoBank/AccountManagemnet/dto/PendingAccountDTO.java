package com.zunoBank.AccountManagemnet.dto;

import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PendingAccountDTO {
    private Long accountId;
    private Long customerId;
    private AccountType accountType;
    private BigDecimal initialDeposit;
    private String branchName;
    private String branchCode;
    private Long createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;
}
