package com.zunoBank.AccountManagemnet.client;

import com.zunoBank.AccountManagemnet.config.FeignConfig;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "http://localhost:8081/api/v1", configuration = FeignConfig.class)
public interface AuthServiceClient {

    @GetMapping("/internal/staff/{employeeId}")
    StaffResponseDto getStaffByEmployeeId(@PathVariable String employeeId);
}