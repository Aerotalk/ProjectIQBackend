package com.grivetyglobals.invoiceiq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class for handling mock email and notification dispatch.
 * Annotates methods with @Async to execute asynchronously.
 */
@Service
@Slf4j
public class NotificationService {

    /**
     * Asynchronously sends a password reset email to the user.
     * 
     * @param to         the recipient's email address
     * @param resetToken the token to reset the password
     */
    @Async
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("======================================================");
        log.info("MOCK EMAIL SERVICE: Sending Password Reset Email");
        log.info("To: {}", to);
        log.info("Subject: Reset Your Password");
        log.info("Body: Click the link below to reset your password:\nhttp://localhost:3000/reset-password?token={}", resetToken);
        log.info("======================================================");
    }

    /**
     * Asynchronously sends a welcome email to a newly registered user.
     * 
     * @param to   the recipient's email address
     * @param name the recipient's name
     */
    @Async
    public void sendWelcomeEmail(String to, String name) {
        log.info("======================================================");
        log.info("MOCK EMAIL SERVICE: Sending Welcome Email");
        log.info("To: {}", to);
        log.info("Subject: Welcome to InvoiceIQ!");
        log.info("Body: Hi {}, welcome aboard! We are thrilled to have you.", name);
        log.info("======================================================");
    }

    /**
     * Asynchronously sends an invitation email to a new employee.
     * 
     * @param to         the recipient's email address
     * @param inviteLink the link to join the organization
     */
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
