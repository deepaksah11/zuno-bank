package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.TransactionResponseDTO;
import com.zunoBank.Transactions.dto.TransferRequestDTO;
import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.feign.AccountFeignClient;
import com.zunoBank.Transactions.repository.TransactionRepository;
import com.zunoBank.Transactions.util.ValidateAmtRules;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    }
