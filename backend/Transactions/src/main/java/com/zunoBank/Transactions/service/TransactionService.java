package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.DepositRequestDTO;
import com.zunoBank.Transactions.dto.TransactionResponseDTO;
import com.zunoBank.Transactions.dto.TransferRequestDTO;
import com.zunoBank.Transactions.dto.WithdrawRequestDTO;
import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.entity.type.TransactionType;
import com.zunoBank.Transactions.feign.AccountFeignClient;
import com.zunoBank.Transactions.repository.TransactionRepository;
import com.zunoBank.Transactions.util.ValidateAmtRules;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;
    private final ValidateAmtRules validateAmtRules;

    private final TransactionValidator validator;
    private final LimitService limitService;
    private final AccountService accountService;
    private final TransactionSave transactionManager;
    private final TransactionMapper transactionMapper;

    @Value("${transaction.daily-limit}")
    private BigDecimal dailyLimit;

    @Value("${transaction.rtgs-min}")
    private BigDecimal rtgsMin;

    @Value("${transaction.imps-max}")
    private BigDecimal impsMax;

    public TransactionResponseDTO transfer(TransferRequestDTO request){
        validateAmtRules.validateAmountRules(request.getType(), request.getAmount());

        limitService.checkDailyLimit(request);

        accountService.transferMoney(request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());

        Transaction txn = transactionManager.saveSuccessTxn(request);

        return transactionMapper.mapToResponse(txn);
    }
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::mapToResponse)
                .toList();
    }

    public TransactionResponseDTO deposit(DepositRequestDTO request) {

        // 1. Credit account
        accountFeignClient.credit(
                request.getAccountNumber(),
                request.getAmount()
        );

        // 2. Create transaction
        Transaction txn = new Transaction();

        txn.setReceiverAccountNumber(request.getAccountNumber());
        txn.setReceiverName("Self");

        txn.setAmount(request.getAmount());
        txn.setType(TransactionType.DEPOSIT);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setDescription(request.getDescription());

        txn.setCompletedAt(LocalDateTime.now());

        transactionRepository.save(txn);

        return transactionMapper.mapToResponse(txn);
    }

    public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {

        // 1. Debit account
        accountFeignClient.debit(
                request.getAccountNumber(),
                request.getAmount()
        );

        // 2. Create transaction
        Transaction txn = new Transaction();

        txn.setSenderAccountNumber(request.getAccountNumber());
        txn.setReceiverName("Self");

        txn.setAmount(request.getAmount());
        txn.setType(TransactionType.WITHDRAW);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setDescription(request.getDescription());

        txn.setCompletedAt(LocalDateTime.now());

        transactionRepository.save(txn);

        return transactionMapper.mapToResponse(txn);
    }

    public BigDecimal getLastMonthDeposits() {

        LocalDate now = LocalDate.now();

        LocalDateTime start = now.minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime end = now.minusMonths(1)
                .withDayOfMonth(
                        now.minusMonths(1).lengthOfMonth()
                )
                .atTime(23, 59, 59);

        return transactionRepository.sumDepositsBetween(start, end);
    }
}
