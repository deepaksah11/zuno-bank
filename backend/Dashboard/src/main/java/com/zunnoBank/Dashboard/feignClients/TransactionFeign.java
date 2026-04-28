package com.zunnoBank.Dashboard.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@FeignClient(name = "TRANSACTION")
public interface TransactionFeign {

    @GetMapping("/api/transactions/count/today")
    long getTodayTransactions();

    @GetMapping("/api/transactions/sum/deposits")
    BigDecimal getTotalDeposits();

    @GetMapping("/api/transactions/sum/deposits/last-month")
    BigDecimal getLastMonthDeposits();

    @GetMapping("/api/transactions/count/yesterday")
    long getYesterdayTransactions();
}