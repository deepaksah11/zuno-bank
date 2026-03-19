package com.zunoBank.AccountManagemnet.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountApprovalDTO {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Manager ID is required")
    private Long managerId;

    private String managerName;

    @NotNull(message = "Approval decision is required")
    private boolean approved;

    private String rejectionReason;
}
