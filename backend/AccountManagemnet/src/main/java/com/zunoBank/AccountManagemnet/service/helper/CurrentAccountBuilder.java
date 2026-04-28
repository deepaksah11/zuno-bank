package com.zunoBank.AccountManagemnet.service.helper;

import com.zunoBank.AccountManagemnet.dto.OnboardingRequestDTO;
import com.zunoBank.AccountManagemnet.dto.OnboardingResponseDTO;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import org.springframework.stereotype.Component;

@Component
public class CurrentAccountBuilder {

    public CurrentAccount buildCurrentAccount(
            OnboardingRequestDTO r, Customer customer, StaffResponseDto staff) {
        CurrentAccount ca = new CurrentAccount();
        ca.setCustomer(customer);
        ca.setCif(customer.getCif());
        ca.setInitialDeposit(r.getInitialDeposit());
        ca.setIfscCode(r.getIfscCode());
        ca.setBranchCode(staff.getBranchCode());
        ca.setBranchName(r.getBranchName());
        ca.setCreatedByRoId(staff.getEmployeeId());
        ca.setRoName(staff.getFullName());
        ca.setStatus(AccountStatus.PENDING_APPROVAL);
        return ca;
    }

    private OnboardingResponseDTO buildCurrentResponse(
            Customer c, CurrentAccount ca) {
        return OnboardingResponseDTO.builder()
                .customerId(c.getId())
                .cif(c.getCif())
                .status(c.getStatus())
                .fullName(c.getFullName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .city(c.getCity())
                .state(c.getState())
                .accountId(ca.getId())
                .accountNumber(ca.getAccountNumber())
                .accountType(AccountType.CURRENT)
                .balance(ca.getBalance())
                .initialDeposit(ca.getInitialDeposit())
                .overdraftLimit(ca.getOverdraftLimit())
                .minimumBalance(ca.getMinimumBalance())
                .ifscCode(ca.getIfscCode())
                .branchCode(ca.getBranchCode())
                .branchName(ca.getBranchName())
                .createdByRoId(ca.getCreatedByRoId())
                .roName(ca.getRoName())
                .submittedAt(ca.getSubmittedAt())
                .approvedByManagerId(c.getApprovedByManagerId())
                .approvedByManagerName(c.getApprovedByManagerName())
                .actionTakenAt(c.getActionTakenAt())
                .build();
    }
}
