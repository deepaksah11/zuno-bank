package com.zunoBank.AccountManagemnet.service.helper;

import com.zunoBank.AccountManagemnet.dto.OnboardingResponseDTO;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilder {

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

    public OnboardingResponseDTO buildCurrentResponse(
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