package com.zunoBank.Transactions.service;

import com.zunoBank.Transactions.entity.Transaction;
import com.zunoBank.Transactions.feign.AccountFeignClient;
import com.zunoBank.Transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountFeignClient accountFeignClient;
    private final TransactionRepository transactionRepository;

    public void transferMoney(String from, String to, BigDecimal amount) {
        try {
                accountFeignClient.debit(from, amount);
                try{
                    accountFeignClient.credit(to, amount);
                } catch (Exception e){
                    accountFeignClient.credit(from, amount);

                    throw new TransactionException("Credit failed refunded");
                }

        } catch (Exception e) {
            throw new TransactionException(
                    "Transfer failed: " + e.getMessage());
        }
    }
}
