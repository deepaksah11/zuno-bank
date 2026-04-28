package com.zunnoBank.Dashboard.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ACCOUNTMANAGEMENT")
public interface CustomerFeign {

    @GetMapping("/api/onboarding/customers/count")
    long getTotalCustomers();

    @GetMapping("/api/onboarding/customers/last-month/count")
    long getLastMonthCustomers();
}