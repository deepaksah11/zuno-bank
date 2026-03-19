package com.zunoBank.AccountManagemnet.dto;


import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponseDTO {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private String branchName;
    private String branchCode;
    private String ifscCode;
    private boolean kycVerified;
    private Long createdByRoId;
    private String roName;
    private Long approvedByManagerId;
    private String managerName;
    private String rejectionReason;
    private LocalDateTime submittedAt;
    private LocalDateTime actionTakenAt;
    private LocalDateTime createdAt;
    private LocalDateTime lastTransactionAt;
}
