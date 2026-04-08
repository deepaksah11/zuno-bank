package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.TransactionResponseDTO;
import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.entity.type.TransactionType;
import com.zunoBank.Transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final TransactionRepository transactionRepository;
    public TransactionResponseDTO mapToResponse(Transaction t) {
        if (t == null) return null;
        return TransactionResponseDTO.builder()
                .id(t.getId())
                .referenceId(t.getReferenceId())
                .senderAccountNumber(t.getSenderAccountNumber())
                .receiverAccountNumber(t.getReceiverAccountNumber())
                .receiverName(t.getReceiverName())
                .amount(t.getAmount())
                .type(t.getType())
                .status(t.getStatus())
                .category(t.getCategory())
                .description(t.getDescription())
                .failureReason(t.getFailureReason())
                .initiatedAt(t.getInitiatedAt())
                .completedAt(t.getCompletedAt())
                .build();
    }

    public List<TransactionResponseDTO> getMiniStatement(
            String accountNumber) {
        return transactionRepository
                .findTop5BySenderAccountNumberOrderByInitiatedAtDesc(
                        accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<TransactionResponseDTO> getHistory(
            String accountNumber,
            TransactionType type,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size){
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("initiatedAt").descending());

        if (type != null || from != null || to != null){
            return transactionRepository
                    .findBySenderAccountNumberAndTypeAndInitiatedAtBetween(
                            accountNumber, type, from, to, pageable)
                    .map(this::mapToResponse);
        }

        return transactionRepository
                .findBySenderAccountNumber(accountNumber, pageable)
                .map(this::mapToResponse);
    }
}
