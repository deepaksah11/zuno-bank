package com.zunoBank.Transactions.controller;


import com.zunoBank.Transactions.dto.*;
import com.zunoBank.Transactions.entity.StandingInstruction;
import com.zunoBank.Transactions.entity.type.TransactionType;
import com.zunoBank.Transactions.repository.TransactionRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // ── Transfer ──────────────────────────────────────────────────────────
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.ok(
                transactionService.transfer(request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @Valid @RequestBody DepositRequestDTO request) {

        return ResponseEntity.ok(transactionService.deposit(request));
    }

    // ✅ WITHDRAW
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Valid @RequestBody WithdrawRequestDTO request) {

        return ResponseEntity.ok(transactionService.withdraw(request));
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

    @GetMapping("/count/today")
    public long countTodayTransactions() {
        return transactionRepository.countByInitiatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59)
        );
    }

    @GetMapping("/sum/deposits")
    public BigDecimal sumDeposits() {
        return transactionRepository.sumByType(TransactionType.DEPOSIT);
    }

    @GetMapping("/sum/deposits/last-month")
    public BigDecimal getLastMonthDeposits() {
        return transactionService.getLastMonthDeposits();
    }

    @GetMapping("/count/yesterday")
    public long getYesterdayTransactions() {
        return transactionRepository.countByInitiatedAtBetween(
                LocalDate.now().minusDays(1).atStartOfDay(),
                LocalDate.now().minusDays(1).atTime(23, 59, 59)
        );
    }
}

