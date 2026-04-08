package com.zunoBank.Transactions.controller;


import com.zunoBank.Transactions.dto.StandingInstructionRequestDTO;
import com.zunoBank.Transactions.entity.StandingInstruction;
import com.zunoBank.Transactions.service.StandingInstructionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standing-instructions")
@RequiredArgsConstructor
public class StandingInstructionController {

    private final StandingInstructionService siService;

    // ── Create ────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<StandingInstruction> create(
            @Valid @RequestBody
            StandingInstructionRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(siService.create(request));
    }

    // ── Get All by CIF ────────────────────────────────────────────────────
    @GetMapping("/{customerCif}")
    public ResponseEntity<List<StandingInstruction>> getAll(
            @PathVariable String customerCif) {
        return ResponseEntity.ok(
                siService.getByCif(customerCif));
    }

    // ── Cancel ────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        siService.cancel(id);
        return ResponseEntity.ok(
                "Standing instruction cancelled");
    }
}
