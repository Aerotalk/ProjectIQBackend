package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.AuthResponse;
import com.grivetyglobals.invoiceiq.dto.LoginRequest;
import com.grivetyglobals.invoiceiq.dto.RegisterRequest;
import com.grivetyglobals.invoiceiq.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/setup-super-admin")
    public ResponseEntity<String> setupSuperAdmin(@Valid @RequestBody RegisterRequest request) {
        authService.setupSuperAdmin(request);
        return ResponseEntity.ok("Super Admin created successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody com.grivetyglobals.invoiceiq.dto.LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Successfully logged out");
    }

    @org.springframework.web.bind.annotation.GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@org.springframework.web.bind.annotation.RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email successfully verified. You can now log in.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody com.grivetyglobals.invoiceiq.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody com.grivetyglobals.invoiceiq.dto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password successfully reset. You can now log in with your new password.");
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ResponseEntity<com.grivetyglobals.invoiceiq.dto.MeResponse> getMe() {
        return ResponseEntity.ok(authService.getMe());
    }

    @org.springframework.web.bind.annotation.GetMapping("/my-companies")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getMyCompanies() {
        return ResponseEntity.ok(authService.getMyCompanies());
    }
}
