package com.zunoBank.AccountManagemnet.dto;


import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import com.zunoBank.AccountManagemnet.entity.type.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OnboardingResponseDTO {

    // Customer
    private Long customerId;
    private String cif;
    private CustomerStatus status;
    private String fullName;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String addressLine1;
    private String pincode;
    private String aadhaarNumber;
    private String panNumber;
    @NotNull
    private LocalDate dateOfBirth;
    private Gender gender;
    private String message;

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
    private String approvedByManagerId;
    private String approvedByManagerName;
    private String rejectionReason;
    private LocalDateTime actionTakenAt;

    // RO
    private String createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;

    private String city;
    private String state;
}
