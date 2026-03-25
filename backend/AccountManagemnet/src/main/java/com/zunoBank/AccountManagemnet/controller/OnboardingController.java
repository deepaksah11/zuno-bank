package com.zunoBank.AccountManagemnet.controller;


import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.service.AccountQueryService;
import com.zunoBank.AccountManagemnet.service.ApprovalService;
import com.zunoBank.AccountManagemnet.service.OnboardingService;
import com.zunoBank.AccountManagemnet.service.PendingQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final ApprovalService approvalService;
    private final AccountQueryService accountQueryService;
    private final PendingQueueService pendingQueueService;
    private final AuthServiceClient authServiceClient;

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
    @GetMapping("/pending/{branchCode}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','BRANCH_MANAGER')")
    public ResponseEntity<List<PendingOnboardingDTO>> getPending(
            @PathVariable String branchCode, @AuthenticationPrincipal UserDetails currentUser) {

        StaffResponseDto staff = authServiceClient
                .getStaffByEmployeeId(currentUser.getUsername());
        return ResponseEntity.ok(
                pendingQueueService.getPendingApplications(branchCode, staff));
    }

    // ── STEP 3: Manager approves or rejects ───────────────────────────────
    @PutMapping("/approve")
    public ResponseEntity<OnboardingResponseDTO> approve(
            @Valid @RequestBody OnboardingApprovalDTO approval, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(
                approvalService.processApproval(approval, currentUser.getUsername()));
    }

    // ── Get customer by CIF ───────────────────────────────────────────────
    @GetMapping("/customer/{cif}")
    public ResponseEntity<OnboardingResponseDTO> getByCif(
            @PathVariable String cif) {
        return ResponseEntity.ok(
                accountQueryService.getByCif(cif));
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

    // ── Get all accounts by CIF ───────────────────────────────────────────
    @GetMapping("/accounts/{cif}")
    public ResponseEntity<CustomerAccountsDTO> getAllAccountsByCif(
            @PathVariable String cif) {
        return ResponseEntity.ok(
                accountQueryService.getAllAccountsByCif(cif));
    }
}
