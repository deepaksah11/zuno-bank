package com.zunoBank.Transactions.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BeneficiaryRequestDTO {
    @NotNull(message = "Customer CIF is required")
    private String customerCif;

    @NotNull(message = "Beneficiary name is required")
    private String name;

    @NotNull(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "IFSC code is required")
    private String ifscCode;

    private String bankName;
}
