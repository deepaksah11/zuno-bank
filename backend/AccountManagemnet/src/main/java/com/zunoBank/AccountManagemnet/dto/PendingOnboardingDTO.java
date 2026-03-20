package com.zunoBank.AccountManagemnet.dto;

import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PendingOnboardingDTO {

    // ── Customer Details ──────────────────────────────────────────────────
    private Long customerId;
    private String cif;                    // null if new customer
    private CustomerStatus status;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String alternatePhone;
    private String email;

    // ── Address ───────────────────────────────────────────────────────────
    private String addressLine1;
    private String city;
    private String state;
    private String pincode;

    // ── KYC ───────────────────────────────────────────────────────────────
    private String aadhaarNumber;
    private String panNumber;

    // ── Occupation ────────────────────────────────────────────────────────
    private String occupationType;
    private String employerName;
    private BigDecimal annualIncome;

    // ── Account Details ───────────────────────────────────────────────────
    private AccountType accountType;       // SAVINGS or CURRENT
    private BigDecimal initialDeposit;
    private String ifscCode;

    // ── Branch & RO ───────────────────────────────────────────────────────
    private String branchCode;
    private String branchName;
    private Long createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;
}
