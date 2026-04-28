package com.zunoBank.AccountManagemnet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountListDTO {
    private Long id;
    private String accountNumber;
    private String accountType; // SAVING / CURRENT
    private String cif;
    private String customerName;
    private BigDecimal balance;
    private String status;
    private String branchCode;
}