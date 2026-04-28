package com.zunnoBank.Dashboard.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "LOAN-SERVICE")
public interface LoanFeign {

    @GetMapping("/api/loans/count/active")
    long getActiveLoans();
}
