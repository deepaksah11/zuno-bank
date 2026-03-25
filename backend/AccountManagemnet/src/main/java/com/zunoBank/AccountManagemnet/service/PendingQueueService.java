package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.PendingOnboardingDTO;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PendingQueueService {
    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public List<PendingOnboardingDTO> getPendingApplications(
            String branchCode, StaffResponseDto staff) {

        List<PendingOnboardingDTO> result = savingAccountRepository
                .findByStatusAndBranchCode(
                        AccountStatus.PENDING_APPROVAL, branchCode)
                .stream()
                .map(sa -> PendingOnboardingDTO.builder()
                        .customerId(sa.getCustomer().getId())
                        .fullName(sa.getCustomer().getFullName())
                        .cif(sa.getCustomer().getCif())
                        .status(sa.getCustomer().getStatus())
                        .phone(sa.getCustomer().getPhone())
                        .email(sa.getCustomer().getEmail())
                        .dateOfBirth(sa.getCustomer().getDateOfBirth())
                        .gender(sa.getCustomer().getGender() != null
                                ? sa.getCustomer().getGender().name()
                                : null)
                        .addressLine1(
                                sa.getCustomer().getAddressLine1())
                        .city(sa.getCustomer().getCity())
                        .state(sa.getCustomer().getState())
                        .pincode(sa.getCustomer().getPincode())
                        .aadhaarNumber(
                                sa.getCustomer().getAadhaarNumber())
                        .panNumber(sa.getCustomer().getPanNumber())
                        .occupationType(
                                sa.getCustomer().getOccupationType() != null
                                        ? sa.getCustomer().getOccupationType().name()
                                        : null)
                        .employerName(sa.getCustomer().getEmployerName())
                        .annualIncome(sa.getCustomer().getAnnualIncome())
                        .accountType(AccountType.SAVINGS)
                        .initialDeposit(sa.getInitialDeposit())
                        .ifscCode(sa.getIfscCode())
                        .branchCode(staff.getBranchCode())
                        .branchName(sa.getBranchName())
                        .createdByRoId(staff.getEmployeeId())
                        .roName(staff.getFullName())
                        .submittedAt(sa.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());

        currentAccountRepository
                .findByStatusAndBranchCode(
                        AccountStatus.PENDING_APPROVAL, branchCode)
                .stream()
                .map(ca -> PendingOnboardingDTO.builder()
                        .customerId(ca.getCustomer().getId())
                        .fullName(ca.getCustomer().getFullName())
                        .cif(ca.getCustomer().getCif())
                        .status(ca.getCustomer().getStatus())
                        .phone(ca.getCustomer().getPhone())
                        .email(ca.getCustomer().getEmail())
                        .dateOfBirth(ca.getCustomer().getDateOfBirth())
                        .gender(ca.getCustomer().getGender() != null
                                ? ca.getCustomer().getGender().name()
                                : null)
                        .addressLine1(
                                ca.getCustomer().getAddressLine1())
                        .city(ca.getCustomer().getCity())
                        .state(ca.getCustomer().getState())
                        .pincode(ca.getCustomer().getPincode())
                        .aadhaarNumber(
                                ca.getCustomer().getAadhaarNumber())
                        .panNumber(ca.getCustomer().getPanNumber())
                        .occupationType(
                                ca.getCustomer().getOccupationType() != null
                                        ? ca.getCustomer().getOccupationType().name()
                                        : null)
                        .employerName(ca.getCustomer().getEmployerName())
                        .annualIncome(ca.getCustomer().getAnnualIncome())
                        .accountType(AccountType.CURRENT)
                        .initialDeposit(ca.getInitialDeposit())
                        .ifscCode(ca.getIfscCode())
                        .branchCode(ca.getBranchCode())
                        .branchName(ca.getBranchName())
                        .createdByRoId(ca.getCreatedByRoId())
                        .roName(ca.getRoName())
                        .submittedAt(ca.getSubmittedAt())
                        .build())
                .forEach(result::add);

        return result;
    }
}
