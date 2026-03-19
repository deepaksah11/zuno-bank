package com.zunoBank.AccountManagemnet.service;




import com.zunoBank.AccountManagemnet.dto.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    // RO creates account request
    AccountResponseDTO createAccountRequest(AccountOpenRequestDTO request);

    // Manager approves or rejects
    AccountResponseDTO processApproval(AccountApprovalDTO approval);

    // Manager views pending queue
    List<PendingAccountDTO> getPendingAccounts(String branchCode);

    // General
    AccountResponseDTO getAccountDetails(String accountNumber);
    List<AccountResponseDTO> getAccountsByCustomer(Long customerId);

    // Statements
    Page<MiniStatementDTO> getStatement(String accountNumber, int page, int size);
    List<MiniStatementDTO> getMiniStatement(String accountNumber);

    // Admin / Manager actions
    void freezeAccount(String accountNumber);
    void unfreezeAccount(String accountNumber);
    void closeAccount(String accountNumber);

    // Internal — called by transaction-service via Feign
    void debit(String accountNumber, BigDecimal amount);
    void credit(String accountNumber, BigDecimal amount);
}
