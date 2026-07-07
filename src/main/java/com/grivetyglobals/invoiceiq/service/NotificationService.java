package com.grivetyglobals.invoiceiq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Async
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("======================================================");
        log.info("MOCK EMAIL SERVICE: Sending Password Reset Email");
        log.info("To: {}", to);
        log.info("Subject: Reset Your Password");
        log.info("Body: Click the link below to reset your password:\nhttp://localhost:3000/reset-password?token={}", resetToken);
        log.info("======================================================");
    }

    @Async
    public void sendWelcomeEmail(String to, String name) {
        log.info("======================================================");
        log.info("MOCK EMAIL SERVICE: Sending Welcome Email");
        log.info("To: {}", to);
        log.info("Subject: Welcome to InvoiceIQ!");
        log.info("Body: Hi {}, welcome aboard! We are thrilled to have you.", name);
        log.info("======================================================");
    }

    @Async
    public void sendEmployeeInviteEmail(String to, String inviteLink) {
        log.info("======================================================");
        log.info("MOCK EMAIL SERVICE: Sending Employee Invite Email");
        log.info("To: {}", to);
        log.info("Subject: You've been invited to join the team!");
        log.info("Body: You have been invited to join the organization on InvoiceIQ. Click the link to set up your account:\n{}", inviteLink);
        log.info("======================================================");
    }
}
