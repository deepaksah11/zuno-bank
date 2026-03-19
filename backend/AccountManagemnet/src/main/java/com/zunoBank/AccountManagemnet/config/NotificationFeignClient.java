package com.zunoBank.AccountManagemnet.config;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationFeignClient {

    @PostMapping("/api/notifications/customer")
    void notifyCustomer(
            @RequestParam Long customerId,
            @RequestParam String message);

    @PostMapping("/api/notifications/manager")
    void notifyManager(
            @RequestParam String branchCode,
            @RequestParam String message);
}
