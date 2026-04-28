package com.zunoBank.AccountManagemnet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AccountsPageDTO {
    private List<AccountListDTO> accounts;

    private long totalSavingAccounts;
    private BigDecimal totalSavingBalance;

    private long totalCurrentAccounts;
    private BigDecimal totalCurrentBalance;
}
