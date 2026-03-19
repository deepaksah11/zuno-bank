package com.zunoBank.AccountManagemnet.config;



import com.zunoBank.AccountManagemnet.dto.MiniStatementDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "transaction-service")
public interface TransactionFeignClient {

    @GetMapping("/api/transactions/mini/{accountNumber}")
    List<MiniStatementDTO> getMiniStatement(
            @PathVariable String accountNumber);

    @GetMapping("/api/transactions/history/{accountNumber}")
    Page<MiniStatementDTO> getFullStatement(
            @PathVariable String accountNumber,
            @RequestParam int page,
            @RequestParam int size);
}
