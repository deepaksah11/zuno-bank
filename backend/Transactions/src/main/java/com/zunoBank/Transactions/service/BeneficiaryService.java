package com.zunoBank.Transactions.service;


import com.zunoBank.Transactions.dto.BeneficiaryRequestDTO;
import com.zunoBank.Transactions.dto.BeneficiaryResponseDTO;
import com.zunoBank.Transactions.entity.Beneficiary;
import com.zunoBank.Transactions.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    // ── Add Beneficiary ───────────────────────────────────────────────────

    public BeneficiaryResponseDTO addBeneficiary(
            BeneficiaryRequestDTO request) {

        if (beneficiaryRepository
                .existsByCustomerCifAndAccountNumber(
                        request.getCustomerCif(),
                        request.getAccountNumber()))
            throw new TransactionException(
                    "Beneficiary already added");

        Beneficiary b = new Beneficiary();
        b.setCustomerCif(request.getCustomerCif());
        b.setName(request.getName());
        b.setAccountNumber(request.getAccountNumber());
        b.setIfscCode(request.getIfscCode());
        b.setBankName(request.getBankName());

        return mapToResponse(beneficiaryRepository.save(b));
    }

    // ── Get All Beneficiaries ─────────────────────────────────────────────

    public List<BeneficiaryResponseDTO> getBeneficiaries(
            String customerCif) {
        return beneficiaryRepository
                .findByCustomerCifAndActive(customerCif, true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Delete Beneficiary ────────────────────────────────────────────────

    public void deleteBeneficiary(Long id) {
        Beneficiary b = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new TransactionException(
                        "Beneficiary not found: " + id));
        b.setActive(false);
        beneficiaryRepository.save(b);
    }

    // ── Validate Cooling Period ───────────────────────────────────────────

    public void validateCoolingPeriod(
            String customerCif, String accountNumber) {
        Beneficiary b = beneficiaryRepository
                .findByCustomerCifAndAccountNumber(
                        customerCif, accountNumber)
                .orElseThrow(() -> new TransactionException(
                        "Beneficiary not found. "
                                + "Please add beneficiary first."));

        if (!b.isCoolingPeriodOver()) {
            long hoursLeft = java.time.temporal.ChronoUnit.HOURS
                    .between(LocalDateTime.now(), b.getCoolingEndsAt());
            throw new TransactionException(
                    "Cooling period active. "
                            + "Transfer allowed after "
                            + hoursLeft + " hour(s)");
        }
    }

    private BeneficiaryResponseDTO mapToResponse(Beneficiary b) {
        return BeneficiaryResponseDTO.builder()
                .id(b.getId())
                .customerCif(b.getCustomerCif())
                .name(b.getName())
                .accountNumber(b.getAccountNumber())
                .ifscCode(b.getIfscCode())
                .bankName(b.getBankName())
                .addedAt(b.getAddedAt())
                .coolingEndsAt(b.getCoolingEndsAt())
                .coolingPeriodOver(b.isCoolingPeriodOver())
                .active(b.isActive())
                .build();
    }
}
