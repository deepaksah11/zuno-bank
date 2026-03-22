package com.zunoBank.Authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendWelcomeEmail(String toEmail, String fullName,
                                 String employeeId, String tempPassword,
                                 String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("${spring.mail.from}");
            message.setTo(toEmail);
            message.setSubject("Welcome to ZunoBank — Your Account Details");
            message.setText(
                    "Hello " + fullName + ",\n\n" +
                            "Your staff account has been created.\n\n" +
                            "Employee ID : " + employeeId + "\n" +
                            "Password    : " + tempPassword + "\n" +
                            "Role        : " + role + "\n\n" +
                            "Please login and change your password immediately.\n" +
                            "Your temporary password expires in 48 hours.\n\n" +
                            "Login at: http://localhost:3000/login\n\n" +
                            "Regards,\n" +
                            "ZunoBank IT Team"
            );

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);

        } catch (Exception ex) {
            log.error("Failed to send email to: {}. Error: {}", toEmail, ex.getMessage());
            // do not throw — email failure should not fail the API response
        }
    }
}