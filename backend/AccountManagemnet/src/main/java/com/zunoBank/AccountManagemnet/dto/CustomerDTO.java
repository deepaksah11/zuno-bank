package com.zunoBank.AccountManagemnet.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String cif;
    private String firstName;
    private String lastName;
    private String phone;
    private String branchCode;
    private String accountType;
    private String balance;
    private String status;
    private String email;
}
