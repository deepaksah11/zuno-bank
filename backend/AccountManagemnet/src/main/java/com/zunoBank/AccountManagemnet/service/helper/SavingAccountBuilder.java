package com.zunoBank.AccountManagemnet.service.helper;

import com.zunoBank.AccountManagemnet.dto.OnboardingRequestDTO;
import com.zunoBank.AccountManagemnet.dto.OnboardingResponseDTO;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import org.springframework.stereotype.Component;

@Component
public class SavingAccountBuilder {
    public SavingAccount buildSavingAccount(
            OnboardingRequestDTO r, Customer customer, StaffResponseDto staff) {
        SavingAccount sa = new SavingAccount();
        sa.setCustomer(customer);
        sa.setCif(customer.getCif());
        sa.setInitialDeposit(r.getInitialDeposit());
        sa.setIfscCode(r.getIfscCode());
        sa.setBranchCode(staff.getBranchCode());     // ← was request.getBranchCode()
        sa.setBranchName(r.getBranchName());
        sa.setCreatedByRoId(staff.getEmployeeId());  // ← was request.getCreatedByRoId()
        sa.setRoName(staff.getFullName());           // ← was request.getRoName()
        sa.setStatus(AccountStatus.PENDING_APPROVAL);
        return sa;
    }

    public OnboardingResponseDTO buildSavingResponse(
            Customer c, SavingAccount sa) {
        return OnboardingResponseDTO.builder()
                .customerId(c.getId())
                .cif(c.getCif())
                .status(c.getStatus())
                .fullName(c.getFullName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .city(c.getCity())
                .state(c.getState())
                .accountId(sa.getId())
                .accountNumber(sa.getAccountNumber())
                .accountType(AccountType.SAVINGS)
                .balance(sa.getBalance())
                .initialDeposit(sa.getInitialDeposit())
                .interestRate(sa.getInterestRate())
                .minimumBalance(sa.getMinimumBalance())
                .ifscCode(sa.getIfscCode())
                .branchCode(sa.getBranchCode())
                .branchName(sa.getBranchName())
                .createdByRoId(sa.getCreatedByRoId())
                .roName(sa.getRoName())
                .submittedAt(sa.getSubmittedAt())
                .approvedByManagerId(c.getApprovedByManagerId())
                .approvedByManagerName(c.getApprovedByManagerName())
                .actionTakenAt(c.getActionTakenAt())
                .build();
    }
}
