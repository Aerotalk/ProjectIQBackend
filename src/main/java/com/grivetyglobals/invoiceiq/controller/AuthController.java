package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.AuthResponse;
import com.grivetyglobals.invoiceiq.dto.LoginRequest;
import com.grivetyglobals.invoiceiq.dto.RegisterRequest;
import com.grivetyglobals.invoiceiq.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setCookie(response, "access_token", authResponse.getToken(), 7 * 24 * 60 * 60);
        setCookie(response, "refresh_token", authResponse.getRefreshToken(), 7 * 24 * 60 * 60);
        authResponse.setToken(null);
        authResponse.setRefreshToken(null);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest request = new com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        AuthResponse authResponse = authService.refreshToken(request);
        setCookie(response, "access_token", authResponse.getToken(), 7 * 24 * 60 * 60);
        setCookie(response, "refresh_token", authResponse.getRefreshToken(), 7 * 24 * 60 * 60);
        authResponse.setToken(null);
        authResponse.setRefreshToken(null);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            com.grivetyglobals.invoiceiq.dto.LogoutRequest request = new com.grivetyglobals.invoiceiq.dto.LogoutRequest();
            request.setRefreshToken(refreshToken);
            authService.logout(request);
        }
        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");
        return ResponseEntity.ok("Successfully logged out");
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Should be true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
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
