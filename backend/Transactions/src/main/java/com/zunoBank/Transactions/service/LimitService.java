package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.TransferRequestDTO;
import com.zunoBank.Transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LimitService {
    private final TransactionRepository transactionRepository;

    @Value("${transaction.daily-limit}")
    private BigDecimal dailyLimit;

    public void checkDailyLimit(TransferRequestDTO request) {
        BigDecimal todayTotal = transactionRepository
            .sumTodayTransactions(
                    request.getSenderAccountNumber(),
                    LocalDateTime.now().toLocalDate().atStartOfDay());
        if(todayTotal.add(request.getAmount())
                .

    compareTo(dailyLimit) >0)
            throw new

    TransactionException(
                    "Daily limit of ₹"+dailyLimit
                            +" exceeded. Used: ₹"+todayTotal);
    }
}
