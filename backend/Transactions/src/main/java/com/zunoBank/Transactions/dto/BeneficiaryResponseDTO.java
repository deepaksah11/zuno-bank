package com.zunoBank.Transactions.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BeneficiaryResponseDTO {
    private Long id;
    private String customerCif;
    private String name;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private LocalDateTime addedAt;
    private LocalDateTime coolingEndsAt;
    private boolean coolingPeriodOver;
    private boolean active;
}
