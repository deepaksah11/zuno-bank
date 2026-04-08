package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.TransferRequestDTO;
import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionSave {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public Transaction saveSuccessTxn(TransferRequestDTO request) {
        Transaction txn = new Transaction();
        txn.setSenderAccountNumber(request.getSenderAccountNumber());
        txn.setSenderCif(request.getSenderCif());
        txn.setReceiverAccountNumber(request.getReceiverAccountNumber());
        txn.setReceiverIfsc(request.getReceiverIfsc());
        txn.setReceiverName(request.getReceiverName());
        txn.setAmount(request.getAmount());
        txn.setType(request.getType());
        txn.setDescription(request.getDescription());
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setCategory(categoryService.autoTag(request.getDescription()));
        txn.setCompletedAt(LocalDateTime.now());

        return transactionRepository.save(txn);
    }


}
