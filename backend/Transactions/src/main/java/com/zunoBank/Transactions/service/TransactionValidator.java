package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.entity.type.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionValidator {
    public static void validateAmountRules(TransactionType type, BigDecimal amount) {
        if (type == null || amount == null) {
            throw new IllegalArgumentException("Transaction type and amount must not be null");
        }
        switch (type) {
            case RTGS -> {
                if (amount.compareTo(new BigDecimal("200000")) < 0)
                    throw new RuntimeException("RTGS minimum is ₹2,00,000");
            }
            case IMPS -> {
                if (amount.compareTo(new BigDecimal("500000")) > 0)
                    throw new RuntimeException("IMPS maximum is ₹5,00,000");
            }
            case NEFT -> {
                // no min/max limit for NEFT
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }
}
