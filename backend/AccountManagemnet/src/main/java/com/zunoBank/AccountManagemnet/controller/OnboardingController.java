package com.zunoBank.AccountManagemnet.controller;


import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.repository.CustomerRepository;
import com.zunoBank.AccountManagemnet.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final ApprovalService approvalService;
    private final AccountQueryService accountQueryService;
    private final PendingQueueService pendingQueueService;
    private final AuthServiceClient authServiceClient;
    private final AccountTransactionService accountTransactionService;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    // ── STEP 1: RO submits ONE form ───────────────────────────────────────
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER','RELATIONSHIP_OFFICER')")
    public ResponseEntity<OnboardingResponseDTO> create(
            @Valid @RequestBody OnboardingRequestDTO request, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(onboardingService.createApplication(request, currentUser.getUsername()));
    }

    // ── STEP 2: Manager views pending queue ───────────────────────────────
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER')")
    public ResponseEntity<List<PendingOnboardingDTO>> getPending( @AuthenticationPrincipal UserDetails currentUser) {

        StaffResponseDto staff = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());
        return ResponseEntity.ok(
                pendingQueueService.getPendingApplications(staff.getBranchCode(), staff));
    }

    // ── STEP 3: Manager approves or rejects ───────────────────────────────
    @PutMapping("/approve")
    public ResponseEntity<OnboardingResponseDTO> approve(
            @Valid @RequestBody OnboardingApprovalDTO approval, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(
                approvalService.processApproval(approval, currentUser.getUsername()));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER','RELATIONSHIP_OFFICER')")
    public ResponseEntity<Page<CustomerDTO>> getCustomers(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        StaffResponseDto user = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());
        Page<CustomerDTO> customers;

        if (user.getRole().equals("SUPER_ADMIN")) {
            customers = accountQueryService.getAllCustomersWithAccounts(page, size); // ✅ Changed
        } else {
            customers = accountQueryService.getCustomersByBranchWithAccounts(user.getBranchCode(), page, size); // ✅ Changed
        }
        log.info(customers.toString());
        return ResponseEntity.ok(customers);
    }

    // ── Get customer by CIF ───────────────────────────────────────────────
    @GetMapping("/customer/{cif}")
    public ResponseEntity<OnboardingResponseDTO> getByCif(
            @PathVariable String cif,
            @AuthenticationPrincipal UserDetails currentUser) {

        StaffResponseDto user = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());

        return ResponseEntity.ok(
                accountQueryService.getByCifAndBranch(cif, user.getBranchCode()));
    }

    // ── Get saving account details ────────────────────────────────────────
    @GetMapping("/saving/{accountNumber}")
    public ResponseEntity<OnboardingResponseDTO> getSavingAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                accountQueryService.getSavingAccount(accountNumber));
    }

    // ── Get current account details ───────────────────────────────────────
    @GetMapping("/current/{accountNumber}")
    public ResponseEntity<OnboardingResponseDTO> getCurrentAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                accountQueryService.getCurrentAccount(accountNumber));
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER','TELLER')")
    public ResponseEntity<AccountsPageDTO> getAllAccounts(
            @AuthenticationPrincipal UserDetails currentUser) {

        StaffResponseDto user = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());

        return ResponseEntity.ok(
                accountQueryService.getAllAccounts(user.getBranchCode(), user.getRole())
        );
    }

    // ── Get all accounts by CIF ───────────────────────────────────────────
    @GetMapping("/accounts/{cif}")
    public ResponseEntity<CustomerAccountsDTO> getAllAccountsByCif(
            @PathVariable String cif, @AuthenticationPrincipal UserDetails currentUser) {

        StaffResponseDto user = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());
        List<CustomerDTO> customers;

        return ResponseEntity.ok(
                accountQueryService.getAllAccountsByCif(cif, user.getBranchCode()));
    }

    // ── Internal: Debit ───────────────────────────────────────────────────
    @PutMapping("/debit")
    public ResponseEntity<Void> debit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountTransactionService.debit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    // ── Internal: Credit ──────────────────────────────────────────────────
    @PutMapping("/credit")
    public ResponseEntity<Void> credit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountTransactionService.credit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customers/count")
    public long countCustomers() {
        return customerRepository.count();
    }

    @GetMapping("/customers/last-month/count")
    public long getLastMonthCustomers() {
        return accountQueryService.getLastMonthCustomers();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER','RELATIONSHIP_OFFICER')")
    public ResponseEntity<OnboardingResponseDTO> update(
            @RequestBody OnboardingRequestDTO updateRequest,
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity.ok(
                onboardingService.updateCustomer(updateRequest, currentUser.getUsername())
        );
    }

    @PutMapping("/close/{cif}")
    public ResponseEntity<String> closeCustomer(
            @PathVariable String cif,
            @AuthenticationPrincipal UserDetails currentUser) {

        onboardingService.closeCustomer(cif, currentUser.getUsername());
        return ResponseEntity.ok("Customer closed successfully");
    }

    @PutMapping("/close-by-id/{id}")
    public ResponseEntity<String> closeCustomerById(@PathVariable Long id) {
        onboardingService.closeCustomerById(id);
        return ResponseEntity.ok("Customer closed successfully");
    }
}
