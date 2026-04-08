package com.zunoBank.AccountManagemnet.dto;

import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.Gender;
import com.zunoBank.AccountManagemnet.entity.type.MaritalStatus;
import com.zunoBank.AccountManagemnet.entity.type.OccupationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OnboardingRequestDTO {

    // Customer Details
    @NotNull
    private String firstName;
    private String middleName;
    @NotNull private String lastName;
    @NotNull private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    @NotNull private String phone;
    private String alternatePhone;
    @NotNull private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String aadhaarNumber;
    private String panNumber;
    private OccupationType occupationType;
    private String employerName;
    private BigDecimal annualIncome;

    // Account Details
    @NotNull private AccountType accountType;
    @NotNull private BigDecimal initialDeposit;
    private String ifscCode;

    // Branch
    private String branchName;

    // REMOVED — branchCode, createdByRoId, roName
    // these now come from JWT + auth-service Feign call

    private String existingCif;
}
