package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Setting;
import com.grivetyglobals.invoiceiq.service.SettingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @Data
    public static class SettingRequest {
        private String key;
        private String value;
        private String category;
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Setting>> getSettings(
            @RequestParam("organizationId") UUID organizationId,
            @RequestParam(value = "category", required = false) String category) {
        
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(settingService.getSettingsByCategory(organizationId, category));
        }
        return ResponseEntity.ok(settingService.getSettingsByOrganization(organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/{key}")
    public ResponseEntity<Setting> getSettingByKey(
            @RequestParam("organizationId") UUID organizationId,
            @PathVariable String key) {
        Setting setting = settingService.getSettingByKey(organizationId, key);
        if (setting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(setting);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping
    public ResponseEntity<Setting> saveSetting(
            @RequestParam("organizationId") UUID organizationId,
            @RequestBody SettingRequest request,
            @AuthenticationPrincipal com.grivetyglobals.invoiceiq.entity.User user) {
        
        UUID userId = (user != null) ? user.getId() : null;
        return ResponseEntity.ok(settingService.saveSetting(organizationId, request.getKey(), request.getValue(), request.getCategory(), userId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(
            @RequestParam("organizationId") UUID organizationId,
            @PathVariable String key,
            @AuthenticationPrincipal com.grivetyglobals.invoiceiq.entity.User user) {
        
        UUID userId = (user != null) ? user.getId() : null;
        settingService.deleteSetting(organizationId, key, userId);
        return ResponseEntity.noContent().build();
    }
}
