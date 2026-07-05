package com.grivetyglobals.invoiceiq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    // In a real app, this would be your frontend URL (e.g., http://localhost:3000)
    private final String appUrl = "http://localhost:8080";

    public void sendVerificationEmail(String to, String token) {
        String verificationLink = appUrl + "/api/auth/verify-email?token=" + token;
        
        System.out.println("\n=======================================================");
        System.out.println("TESTING MODE: Verification Link Generated for " + to + "!");
        System.out.println("Click here to verify: " + verificationLink);
        System.out.println("=======================================================\n");
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = appUrl + "/reset-password?token=" + token;
        
        System.out.println("\n=======================================================");
        System.out.println("TESTING MODE: Password Reset Link Generated for " + to + "!");
        System.out.println("Click here to reset: " + resetLink);
        System.out.println("=======================================================\n");
    }
}
