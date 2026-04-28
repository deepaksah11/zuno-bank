package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.OnboardingApprovalDTO;
import com.zunoBank.AccountManagemnet.dto.OnboardingResponseDTO;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import com.zunoBank.AccountManagemnet.error.AccountException;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.CustomerRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import com.zunoBank.AccountManagemnet.service.helper.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApprovalService {
    private final AuthServiceClient authServiceClient;
    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final ResponseBuilder responseBuilder;

    @Transactional
    public OnboardingResponseDTO processApproval(
            OnboardingApprovalDTO approval, String employeeId) {

        StaffResponseDto manager = authServiceClient
                .getStaffByEmployeeId(employeeId);

        Customer customer = customerRepository
                .findById(approval.getCustomerId())
                .orElseThrow(() -> new AccountException(
                        "Customer not found: #"
                                + approval.getCustomerId()));

        // find pending account
        Optional<SavingAccount> pendingSaving =
                savingAccountRepository
                        .findByCustomerAndStatus(
                                customer,
                                AccountStatus.PENDING_APPROVAL);

        Optional<CurrentAccount> pendingCurrent =
                currentAccountRepository
                        .findByCustomerAndStatus(
                                customer,
                                AccountStatus.PENDING_APPROVAL);

        if (pendingSaving.isEmpty() && pendingCurrent.isEmpty())
            throw new AccountException(
                    "No pending account found for customer: #"
                            + approval.getCustomerId());

        if (approval.isApproved()) {

            // ── generate CIF only if new customer ─────────────────────
            if (customer.getCif() == null) {
                customer.generateCif();
                customer.setStatus(CustomerStatus.ACTIVE);
            }

            customer.setApprovedByManagerId(manager.getEmployeeId());
            customer.setApprovedByManagerName(
                    manager.getFullName());
            customer.setActionTakenAt(LocalDateTime.now());
            customerRepository.save(customer);

            // ── approve saving account ────────────────────────────────
            if (pendingSaving.isPresent()) {
                SavingAccount sa = pendingSaving.get();
                sa.setCif(customer.getCif());
                sa.setApprovedByManagerId(manager.getEmployeeId());
                sa.setApprovedByManagerName(manager.getFullName());
                sa.setActionTakenAt(LocalDateTime.now());
                sa.generateAccountNumber();
                sa.setStatus(AccountStatus.ACTIVE);
                savingAccountRepository.save(sa);

                return responseBuilder.buildSavingResponse(customer, sa);
            }

            // ── approve current account ───────────────────────────────
            if (pendingCurrent.isPresent()) {
                CurrentAccount ca = pendingCurrent.get();
                ca.setCif(customer.getCif());
                ca.setApprovedByManagerId(manager.getEmployeeId());
                ca.setApprovedByManagerName(manager.getFullName());
                ca.setActionTakenAt(LocalDateTime.now());
                ca.generateAccountNumber();
                ca.setStatus(AccountStatus.ACTIVE);
                currentAccountRepository.save(ca);

                return responseBuilder.buildCurrentResponse(customer, ca);
            }

        } else {

            // ── REJECT ────────────────────────────────────────────────
            if (approval.getRejectionReason() == null
                    || approval.getRejectionReason().isBlank())
                throw new AccountException(
                        "Rejection reason is mandatory");

            // reject customer only if new (no CIF)
            if (customer.getCif() == null) {
                customer.setStatus(CustomerStatus.REJECTED);
                customer.setRejectionReason(
                        approval.getRejectionReason());
                customer.setApprovedByManagerId(manager.getEmployeeId());
                customer.setApprovedByManagerName(manager.getFullName());
                customer.setActionTakenAt(LocalDateTime.now());
                customerRepository.save(customer);
            }

            // reject saving account
            pendingSaving.ifPresent(sa -> {
                sa.setStatus(AccountStatus.REJECTED);
                sa.setRejectionReason(
                        approval.getRejectionReason());
                sa.setApprovedByManagerId(manager.getEmployeeId());
                sa.setActionTakenAt(LocalDateTime.now());
                savingAccountRepository.save(sa);
            });

            // reject current account
            pendingCurrent.ifPresent(ca -> {
                ca.setStatus(AccountStatus.REJECTED);
                ca.setRejectionReason(
                        approval.getRejectionReason());
                ca.setApprovedByManagerId(manager.getEmployeeId());
                ca.setActionTakenAt(LocalDateTime.now());
                currentAccountRepository.save(ca);
            });
        }

        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .rejectionReason(customer.getRejectionReason())
                .build();
    }
}
