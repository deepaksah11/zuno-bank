package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import com.zunoBank.AccountManagemnet.error.AccountException;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.CustomerRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import com.zunoBank.AccountManagemnet.service.helper.CurrentAccountBuilder;
import com.zunoBank.AccountManagemnet.service.helper.CustomerBuilder;
import com.zunoBank.AccountManagemnet.service.helper.SavingAccountBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final AuthServiceClient authServiceClient;
    private final CustomerBuilder customerBuilder;
    private final SavingAccountBuilder savingAccountBuilder;
    private final CurrentAccountBuilder currentAccountBuilder;

    // ─────────────────────────────────────────────────────────────────────
    // STEP 1: RO submits ONE form
    // ─────────────────────────────────────────────────────────────────────

    @Transactional
    public OnboardingResponseDTO createApplication(
            OnboardingRequestDTO request, String employeeId) {

        StaffResponseDto staff = authServiceClient
                .getStaffByEmployeeId(employeeId);

        Customer customer;

        if (request.getExistingCif() != null
                && !request.getExistingCif().isBlank()) {

            // ── EXISTING CUSTOMER ─────────────────────────────────────
            if (staff.getRole().equals("SUPER_ADMIN")) {
                customer = customerRepository.findByCif(request.getExistingCif())
                        .orElseThrow();
            } else {
                customer = customerRepository
                        .findByCifAndBranchCode(
                                request.getExistingCif(),
                                staff.getBranchCode()
                        )
                        .orElseThrow(() -> new AccountException(
                                "Customer not found with CIF: "
                                        + request.getExistingCif()));
            }

            if (customer.getStatus() != CustomerStatus.ACTIVE)
                throw new AccountException(
                        "Customer is not active. Status: "
                                + customer.getStatus());

            // check duplicate account
            if (request.getAccountType() == AccountType.SAVINGS) {
                if (savingAccountRepository
                        .existsByCustomer(customer))
                    throw new AccountException(
                            "Customer already has a Saving Account");
            } else {
                if (currentAccountRepository
                        .existsByCustomer(customer))
                    throw new AccountException(
                            "Customer already has a Current Account");
            }

        } else {

            // ── NEW CUSTOMER ──────────────────────────────────────────

            // duplicate checks
            if (customerRepository.existsByPhone(request.getPhone()))
                throw new AccountException(
                        "Phone already registered: "
                                + request.getPhone());

            if (customerRepository.existsByEmail(request.getEmail()))
                throw new AccountException(
                        "Email already registered: "
                                + request.getEmail());

            if (request.getAadhaarNumber() != null &&
                    customerRepository.existsByAadhaarNumber(
                            request.getAadhaarNumber()))
                throw new AccountException(
                        "Aadhaar already registered");

            if (request.getPanNumber() != null &&
                    customerRepository.existsByPanNumber(
                            request.getPanNumber()))
                throw new AccountException(
                        "PAN already registered");

            // save new customer
            customer = customerBuilder.buildCustomer(request, staff);
            customer = customerRepository.save(customer);
        }

        // ── min deposit validation ────────────────────────────────────
        BigDecimal minDeposit = request.getAccountType()
                == AccountType.SAVINGS
                ? new BigDecimal("500")
                : new BigDecimal("5000");

        if (request.getInitialDeposit().compareTo(minDeposit) < 0)
            throw new AccountException(
                    "Minimum deposit for "
                            + request.getAccountType()
                            + " is ₹" + minDeposit);

        // ── Save Account ──────────────────────────────────────────────
        if (request.getAccountType() == AccountType.SAVINGS) {
            SavingAccount sa = savingAccountBuilder.buildSavingAccount(request, customer, staff);
            savingAccountRepository.save(sa);
        } else {
            CurrentAccount ca = currentAccountBuilder.buildCurrentAccount(request, customer, staff);
            currentAccountRepository.save(ca);
        }

        // ── Build Response ────────────────────────────────────────────
        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .accountType(request.getAccountType())
                .initialDeposit(request.getInitialDeposit())
                .branchName(request.getBranchName())
                .branchCode(staff.getBranchCode())      // ← add this
                .createdByRoId(staff.getEmployeeId())   // ← add this
                .roName(staff.getFullName())            // ← add this
                .submittedAt(LocalDateTime.now())
                .build();
    }
}