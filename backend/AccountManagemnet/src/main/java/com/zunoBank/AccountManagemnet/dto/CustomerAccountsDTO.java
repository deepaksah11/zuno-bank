package com.zunoBank.AccountManagemnet.dto;

import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerAccountsDTO {

    // ── Customer ──────────────────────────────────────────────────────────
    private Long customerId;
    private String cif;
    private CustomerStatus status;
    private String fullName;
    private String phone;
    private String alternatePhone;
    private String email;
    private String city;
    private String state;
    private String pincode;

    // ── Accounts ──────────────────────────────────────────────────────────
    // null if not opened
    private SavingAccountDTO savingAccount;
    private CurrentAccountDTO currentAccount;
}