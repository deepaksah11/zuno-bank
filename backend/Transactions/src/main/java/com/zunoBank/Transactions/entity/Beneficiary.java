package com.zunoBank.Transactions.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiary")
@Data
@NoArgsConstructor
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerCif;           // who added this beneficiary
    private String name;
    private String accountNumber;
    private String ifscCode;
    private String bankName;

    private LocalDateTime addedAt;
    private LocalDateTime coolingEndsAt;

    private boolean active;

    @PrePersist
    public void prePersist(){
        this.addedAt = LocalDateTime.now();
        this.coolingEndsAt = this.addedAt.plusHours(24);
        this.active=true;
    }

    public boolean isCoolingPeriodOver(){
        return LocalDateTime.now().isAfter(this.coolingEndsAt);
    }
}
