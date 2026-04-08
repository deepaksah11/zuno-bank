package com.zunoBank.Transactions.controller;


import com.zunoBank.Transactions.dto.StandingInstructionRequestDTO;
import com.zunoBank.Transactions.dto.TransactionResponseDTO;
import com.zunoBank.Transactions.dto.TransferRequestDTO;
import com.zunoBank.Transactions.entity.StandingInstruction;
import com.zunoBank.Transactions.entity.type.TransactionType;
import com.zunoBank.Transactions.service.StandingInstructionService;
import com.zunoBank.Transactions.service.TransactionMapper;
import com.zunoBank.Transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;


    // ── Transfer ──────────────────────────────────────────────────────────
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.ok(
                transactionService.transfer(request));
    }

    // ── Transaction History ───────────────────────────────────────────────
    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<Page<TransactionResponseDTO>> history(
            @PathVariable String accountNumber,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                transactionMapper.getHistory(
                        accountNumber, type, from, to, page, size));
    }

    // ── Mini Statement ────────────────────────────────────────────────────
    @GetMapping("/mini/{accountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> mini(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                transactionMapper.getMiniStatement(accountNumber));
    }

}

