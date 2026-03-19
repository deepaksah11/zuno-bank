package com.zunoBank.AccountManagemnet.controller;


import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.service.AccountNumberGeneratorService;
import com.zunoBank.AccountManagemnet.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountNumberGeneratorService accountNumberGenerator;

    @PostMapping("/request")
    @PreAuthorize("hasRole('RO')")
    public ResponseEntity<AccountResponseDTO> createRequest(
            @Valid @RequestBody AccountOpenRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccountRequest(request));
    }

    @GetMapping("/pending/{branchCode}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<PendingAccountDTO>> getPending(
            @PathVariable String branchCode) {
        return ResponseEntity.ok(accountService.getPendingAccounts(branchCode));
    }

    @PutMapping("/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AccountResponseDTO> processApproval(
            @Valid @RequestBody AccountApprovalDTO approval) {
        return ResponseEntity.ok(accountService.processApproval(approval));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getDetails(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountDetails(accountNumber));
    }

    // ── Get all accounts for a customer ──────────────────────────────────
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponseDTO>> getByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomer(customerId));
    }

    // ── Full paginated statement ──────────────────────────────────────────
    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<Page<MiniStatementDTO>> getStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                accountService.getStatement(accountNumber, page, size));
    }

    // ── Mini statement (last 5) ───────────────────────────────────────────
    @GetMapping("/{accountNumber}/mini-statement")
    public ResponseEntity<List<MiniStatementDTO>> getMiniStatement(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getMiniStatement(accountNumber));
    }

    // ── Manager/Admin: Freeze account ─────────────────────────────────────
    @PutMapping("/{accountNumber}/freeze")
//    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<String> freeze(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
        return ResponseEntity.ok("Account frozen successfully");
    }

    // ── Manager/Admin: Unfreeze account ──────────────────────────────────
    @PutMapping("/{accountNumber}/unfreeze")
//    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<String> unfreeze(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
        return ResponseEntity.ok("Account unfrozen successfully");
    }

    // ── Admin: Close account (soft delete) ───────────────────────────────
    @DeleteMapping("/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> close(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
        return ResponseEntity.ok("Account closed successfully");
    }

    // ── Internal: Debit (called by transaction-service) ───────────────────
    @PutMapping("/debit")
    public ResponseEntity<Void> debit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountService.debit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    // ── Internal: Credit (called by transaction-service) ──────────────────
    @PutMapping("/credit")
    public ResponseEntity<Void> credit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountService.credit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate/{accountNumber}")
    public ResponseEntity<Map<String, Object>> validate(
            @PathVariable String accountNumber) {

        boolean valid = accountNumberGenerator
                .validateAccountNumber(accountNumber);

        return ResponseEntity.ok(Map.of(
                "accountNumber", accountNumber,
                "valid", valid
        ));
    }
}
