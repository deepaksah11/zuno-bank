package com.zunoBank.Authentication.service;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String fullName,
                          String employeeId, String tempPassword,
                          String role);
}
