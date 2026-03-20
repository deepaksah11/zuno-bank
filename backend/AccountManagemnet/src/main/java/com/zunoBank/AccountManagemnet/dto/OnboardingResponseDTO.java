package com.zunoBank.AccountManagemnet.dto;


import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OnboardingResponseDTO {

    // Customer
    private Long customerId;
    private String cif;
    private CustomerStatus status;
    private String fullName;
    private String phone;
    private String email;

    // Account
    private Long accountId;
    private String accountNumber;
    private String ifscCode;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal initialDeposit;
    private BigDecimal minimumBalance;

    // Savings specific
    private BigDecimal interestRate;

    // Current specific
    private BigDecimal overdraftLimit;

    // Branch
    private String branchCode;
    private String branchName;

    // Manager
    private Long approvedByManagerId;
    private String approvedByManagerName;
    private String rejectionReason;
    private LocalDateTime actionTakenAt;

    // RO
    private Long createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;

    private String city;
    private String state;
}
