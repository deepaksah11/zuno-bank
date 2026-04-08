package com.zunoBank.Transactions.controller;


import com.zunoBank.Transactions.dto.BeneficiaryRequestDTO;
import com.zunoBank.Transactions.dto.BeneficiaryResponseDTO;
import com.zunoBank.Transactions.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    // ── Add Beneficiary ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<BeneficiaryResponseDTO> add(
            @Valid @RequestBody BeneficiaryRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(beneficiaryService.addBeneficiary(request));
    }

    // ── Get All Beneficiaries ─────────────────────────────────────────────
    @GetMapping("/{customerCif}")
    public ResponseEntity<List<BeneficiaryResponseDTO>> getAll(
            @PathVariable String customerCif) {
        return ResponseEntity.ok(
                beneficiaryService.getBeneficiaries(customerCif));
    }

    // ── Delete Beneficiary ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok("Beneficiary removed");
    }
}