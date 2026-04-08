package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.dto.StandingInstructionRequestDTO;
import com.zunoBank.Transactions.entity.StandingInstruction;
import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.entity.type.SpendingCategory;
import com.zunoBank.Transactions.entity.type.TransactionStatus;
import com.zunoBank.Transactions.feign.AccountFeignClient;
import com.zunoBank.Transactions.repository.StandingInstructionRepository;
import com.zunoBank.Transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StandingInstructionService {

    private final StandingInstructionRepository siRepository;
    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;

    // ── Create Standing Instruction ───────────────────────────────────────

    public StandingInstruction create(
            StandingInstructionRequestDTO request) {

        StandingInstruction si = new StandingInstruction();
        si.setCustomerCif(request.getCif());
        si.setSenderAccountNumber(
                request.getSenderAccountNumber());
        si.setReceiverAccountNumber(
                request.getReceiverAccountNumber());
        si.setReceiverIfsc(request.getReceiverIfsc());
        si.setReceiverName(request.getReceiverName());
        si.setAmount(request.getAmount());
        si.setType(request.getType());
        si.setFrequency(request.getFrequency());
        si.setNextExecutionAt(request.getFirstExecutionAt());

        return siRepository.save(si);
    }

    // ── Get All by CIF ────────────────────────────────────────────────────

    public List<StandingInstruction> getByCif(String customerCif) {
        return siRepository.findByCustomerCif(customerCif);
    }

    // ── Cancel ────────────────────────────────────────────────────────────

    public void cancel(Long id) {
        StandingInstruction si = siRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Standing instruction not found: " + id));
        si.setActive(false);
        siRepository.save(si);
    }

    // ── Scheduler — runs every hour ───────────────────────────────────────

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void executeDueInstructions() {

        List<StandingInstruction> dueList = siRepository
                .findByActiveAndNextExecutionAtBefore(
                        true, LocalDateTime.now());

        dueList.forEach(si -> {
            try {
                // debit sender
                accountFeignClient.debit(
                        si.getSenderAccountNumber(),
                        si.getAmount());

                // credit receiver
                accountFeignClient.credit(
                        si.getReceiverAccountNumber(),
                        si.getAmount());

                // save transaction record
                Transaction txn = new Transaction();
                txn.setSenderAccountNumber(
                        si.getSenderAccountNumber());
                txn.setReceiverAccountNumber(
                        si.getReceiverAccountNumber());
                txn.setReceiverIfsc(si.getReceiverIfsc());
                txn.setAmount(si.getAmount());
                txn.setType(si.getType());
                txn.setDescription(
                        "Standing Instruction - "
                                + si.getFrequency());
                txn.setStatus(TransactionStatus.SUCCESS);
                txn.setCategory(SpendingCategory.OTHERS);
                txn.setCompletedAt(LocalDateTime.now());
                transactionRepository.save(txn);

                // update next execution
                si.setNextExecutionAt(computeNext(si));
                siRepository.save(si);

            } catch (Exception e) {
                System.err.println(
                        "[SI Scheduler] Failed for SI #"
                                + si.getId() + ": " + e.getMessage());
            }
        });
    }

    private LocalDateTime computeNext(StandingInstruction si) {
        return switch (si.getFrequency()) {
            case "DAILY"   -> si.getNextExecutionAt().plusDays(1);
            case "WEEKLY"  -> si.getNextExecutionAt().plusWeeks(1);
            case "MONTHLY" -> si.getNextExecutionAt().plusMonths(1);
            default -> throw new RuntimeException(
                    "Unknown frequency: " + si.getFrequency());
        };
    }
}