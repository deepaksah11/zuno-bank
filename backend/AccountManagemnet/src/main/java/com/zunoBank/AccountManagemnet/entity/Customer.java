package com.zunoBank.AccountManagemnet.entity;


import com.zunoBank.AccountManagemnet.entity.type.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String cif;                    // NULL until manager approves

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;         // PENDING_APPROVAL → ACTIVE

    // ── Personal Details ──────────────────────────────────────────────────
    private String firstName;
    private String middleName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private String nationality;

    // ── Contact ───────────────────────────────────────────────────────────
    private String phone;

    private String email;

    // ── Address ───────────────────────────────────────────────────────────
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    // ── KYC ───────────────────────────────────────────────────────────────
    @Column(unique = true)
    private String aadhaarNumber;

    @Column(unique = true)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private LocalDateTime kycVerifiedAt;

    // ── Occupation ────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private OccupationType occupationType;

    private String employerName;

    @Column(precision = 15, scale = 2)
    private BigDecimal annualIncome;

    // ── Branch & RO ───────────────────────────────────────────────────────
    private String branchCode;
    private String branchName;
    private Long createdByRoId;
    private String roName;
    private LocalDateTime submittedAt;

    // ── Manager ───────────────────────────────────────────────────────────
    private Long approvedByManagerId;
    private String approvedByManagerName;
    private LocalDateTime actionTakenAt;
    private String rejectionReason;

    // ── Relationships ─────────────────────────────────────────────────────
    @OneToOne(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private SavingAccount savingAccount;

    @OneToOne(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private CurrentAccount currentAccount;

    // ── Timestamps ────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.submittedAt = LocalDateTime.now();
        this.status = CustomerStatus.PENDING_APPROVAL;
        this.kycStatus = KycStatus.PENDING;
        this.nationality = "Indian";
        this.country = "India";
        // cif = null until manager approves
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // called when manager approves
    public void generateCif() {
        this.cif = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d",
                (int)(Math.random() * 9000) + 1000);
        this.status = CustomerStatus.ACTIVE;
    }

    public String getFullName() {
        if (middleName != null && !middleName.isBlank())
            return firstName + " " + middleName + " " + lastName;
        return firstName + " " + lastName;
    }
}
