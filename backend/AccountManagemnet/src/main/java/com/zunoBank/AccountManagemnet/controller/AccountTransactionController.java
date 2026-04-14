package com.zunoBank.AccountManagemnet.controller;


import com.zunoBank.AccountManagemnet.service.AccountTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService accountTransactionService;

    @PutMapping("/debit")
    public ResponseEntity<Void> debit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountTransactionService.debit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/credit")
    public ResponseEntity<Void> credit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        accountTransactionService.credit(accountNumber, amount);
        return ResponseEntity.noContent().build();
    }
}