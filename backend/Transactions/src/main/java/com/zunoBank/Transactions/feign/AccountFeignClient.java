package com.zunoBank.Transactions.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "AccountManagement",
        contextId = "accountFeignClient")
public interface AccountFeignClient {
    @PutMapping("/api/v1/accounts/debit")
    void debit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount);

    @PutMapping("/api/v1/accounts/credit")
    void credit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount);
}
