package com.zunoBank.AccountManagemnet.controller;


import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    // ── STEP 1: RO submits ONE form ───────────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<OnboardingResponseDTO> create(
            @Valid @RequestBody OnboardingRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(onboardingService.createApplication(request));
    }

    // ── STEP 2: Manager views pending queue ───────────────────────────────
    @GetMapping("/pending/{branchCode}")
    public ResponseEntity<List<PendingOnboardingDTO>> getPending(
            @PathVariable String branchCode) {
        return ResponseEntity.ok(
                onboardingService.getPendingApplications(branchCode));
    }

    // ── STEP 3: Manager approves or rejects ───────────────────────────────
    @PutMapping("/approve")
    public ResponseEntity<OnboardingResponseDTO> approve(
            @Valid @RequestBody OnboardingApprovalDTO approval) {
        return ResponseEntity.ok(
                onboardingService.processApproval(approval));
    }

    // ── Get customer by CIF ───────────────────────────────────────────────
    @GetMapping("/customer/{cif}")
    public ResponseEntity<OnboardingResponseDTO> getByCif(
            @PathVariable String cif) {
        return ResponseEntity.ok(
                onboardingService.getByCif(cif));
    }

    // ── Get saving account details ────────────────────────────────────────
    @GetMapping("/saving/{accountNumber}")
    public ResponseEntity<OnboardingResponseDTO> getSavingAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                onboardingService.getSavingAccount(accountNumber));
    }

    // ── Get current account details ───────────────────────────────────────
    @GetMapping("/current/{accountNumber}")
    public ResponseEntity<OnboardingResponseDTO> getCurrentAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                onboardingService.getCurrentAccount(accountNumber));
    }

    // ── Get all accounts by CIF ───────────────────────────────────────────
    @GetMapping("/accounts/{cif}")
    public ResponseEntity<CustomerAccountsDTO> getAllAccountsByCif(
            @PathVariable String cif) {
        return ResponseEntity.ok(
                onboardingService.getAllAccountsByCif(cif));
    }
}
