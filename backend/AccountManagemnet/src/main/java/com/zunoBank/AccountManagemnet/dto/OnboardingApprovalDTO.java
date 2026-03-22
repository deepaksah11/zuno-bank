package com.zunoBank.AccountManagemnet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class OnboardingApprovalDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Manager ID is required")
    private Long managerId;

    private String managerName;

    @NotNull(message = "Approval decision is required")
    private Boolean approved;             // true = approve, false = reject

    private String rejectionReason;       // required only if approved = false
    public boolean isApproved() {
        return approved;
    }
}