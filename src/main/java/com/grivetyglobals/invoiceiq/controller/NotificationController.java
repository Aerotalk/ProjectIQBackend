package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/test-welcome")
    public ResponseEntity<String> testWelcomeEmail(@RequestParam String to, @RequestParam String name) {
        notificationService.sendWelcomeEmail(to, name);
        return ResponseEntity.ok("Welcome email mock triggered. Check console logs.");
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/test-invite")
    public ResponseEntity<String> testInviteEmail(@RequestParam String to, @RequestParam String inviteLink) {
        notificationService.sendEmployeeInviteEmail(to, inviteLink);
        return ResponseEntity.ok("Invite email mock triggered. Check console logs.");
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/test-password-reset")
    public ResponseEntity<String> testPasswordResetEmail(@RequestParam String to, @RequestParam String token) {
        notificationService.sendPasswordResetEmail(to, token);
        return ResponseEntity.ok("Password reset email mock triggered. Check console logs.");
    }
}
