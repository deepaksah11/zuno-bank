package com.zunoBank.AccountManagemnet.service;


import com.zunoBank.AccountManagemnet.entity.AccountSequence;
import com.zunoBank.AccountManagemnet.entity.AccountSequenceId;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.repository.AccountSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorService {

    private final AccountSequenceRepository sequenceRepository;


    @Transactional
    public String generate(String branchCode, AccountType accountType) {

        int typeDigit = getTypeDigit(accountType);

        AccountSequenceId seqId = new AccountSequenceId(branchCode, typeDigit);

        AccountSequence sequence = sequenceRepository
                .findByIdWithLock(seqId)
                .orElseGet(() -> {
                    AccountSequence newSeq = new AccountSequence();
                    newSeq.setId(seqId);
                    newSeq.setLastSequence(0);
                    return newSeq;
                });

        int nextSeq = sequence.getLastSequence() + 1;

        if (nextSeq > 99999) {
            throw new RuntimeException(
                    "Sequence exhausted for branch "
                            + branchCode + " type " + typeDigit);
        }

        sequence.setLastSequence(nextSeq);
        sequenceRepository.save(sequence);

        // Step 2 — build base 10-digit number (without check digit)
        // BBBB + T + UUUUU
        String base = branchCode                          // 4 digits
                + typeDigit                           // 1 digit
                + String.format("%05d", nextSeq);     // 5 digits

        // Step 3 — compute check digit using Luhn algorithm
        int checkDigit = computeLuhnCheckDigit(base);

        // Step 4 — final 11-digit account number
        return base + checkDigit;
    }

    // ── Type digit mapping ────────────────────────────────────────────────

    private int getTypeDigit(AccountType type) {
        return switch (type) {
            case SAVINGS -> 1;
            case CURRENT -> 2;
        };
    }

    // ── Luhn Check Digit ──────────────────────────────────────────────────

    /**
     * Standard Luhn Algorithm:
     * 1. Double every second digit from right
     * 2. If doubled value > 9, subtract 9
     * 3. Sum all digits
     * 4. Check digit = (10 - (sum % 10)) % 10
     */
    public int computeLuhnCheckDigit(String number) {
        int sum = 0;
        boolean doubleDigit = true;  // rightmost digit of base gets doubled first

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return (10 - (sum % 10)) % 10;
    }

    // ── Luhn Validation ───────────────────────────────────────────────────

    /**
     * Validates a full 11-digit account number using Luhn
     */
    public boolean validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 11) return false;

        int sum = 0;
        boolean doubleDigit = false;

        for (int i = accountNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(accountNumber.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return sum % 10 == 0;
    }
}
