package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for triggering and testing mock email notifications.
 */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Triggers a mock welcome email.
     * Requires 'setting.edit' authority.
     *
     * @param to   the recipient's email address
     * @param name the recipient's name
     * @return a success message
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping("/test-welcome")
    public ResponseEntity<String> testWelcomeEmail(@RequestParam String to, @RequestParam String name) {
        notificationService.sendWelcomeEmail(to, name);
        return ResponseEntity.ok("Welcome email mock triggered. Check console logs.");
    }

    /**
     * Triggers a mock employee invitation email.
     * Requires 'setting.edit' authority.
     *
     * @param to         the recipient's email address
     * @param inviteLink the invitation link to include
     * @return a success message
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping("/test-invite")
    public ResponseEntity<String> testInviteEmail(@RequestParam String to, @RequestParam String inviteLink) {
        notificationService.sendEmployeeInviteEmail(to, inviteLink);
        return ResponseEntity.ok("Invite email mock triggered. Check console logs.");
    }

    /**
     * Triggers a mock password reset email.
     * Requires 'setting.edit' authority.
     *
     * @param to    the recipient's email address
     * @param token the password reset token to include
     * @return a success message
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping("/test-password-reset")
    public ResponseEntity<String> testPasswordResetEmail(@RequestParam String to, @RequestParam String token) {
        notificationService.sendPasswordResetEmail(to, token);
        return ResponseEntity.ok("Password reset email mock triggered. Check console logs.");
    }
}
