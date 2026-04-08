package com.zunoBank.AccountManagemnet.dto;

import lombok.Data;

@Data
public class StaffResponseDto {
    private String employeeId;
    private String fullName;
    private String role;
    private String branchCode;
}