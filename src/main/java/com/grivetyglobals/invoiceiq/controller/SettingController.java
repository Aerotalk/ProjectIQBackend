package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Setting;
import com.grivetyglobals.invoiceiq.service.SettingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing global or categorized system settings.
 * Key-value pair configuration accessible by administrators.
 */
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

    /**
     * Retrieves all system settings, optionally filtered by category.
     * Requires 'setting.view' authority.
     *
     * @param category the setting category (optional)
     * @return a list of Setting entities
     */
    @PreAuthorize("hasAuthority('setting.view')")
    @GetMapping
    public ResponseEntity<List<Setting>> getSettings(
            @RequestParam(value = "category", required = false) String category) {
        
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(settingService.getSettingsByCategory(category));
        }
        return ResponseEntity.ok(settingService.getSettingsByOrganization());
    }

    /**
     * Retrieves a specific system setting by its key.
     * Requires 'setting.view' authority.
     *
     * @param key the unique key of the setting
     * @return the Setting entity, or 404 if not found
     */
    @PreAuthorize("hasAuthority('setting.view')")
    @GetMapping("/{key}")
    public ResponseEntity<Setting> getSettingByKey(
            @PathVariable String key) {
        Setting setting = settingService.getSettingByKey(key);
        if (setting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(setting);
    }

    /**
     * Creates or updates a system setting.
     * Requires 'setting.edit' authority.
     *
     * @param request the setting details containing key, value, and category
     * @return the saved Setting entity
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping
    public ResponseEntity<Setting> saveSetting(
            @RequestBody SettingRequest request) {
        
        return ResponseEntity.ok(settingService.saveSetting(request.getKey(), request.getValue(), request.getCategory()));
    }

    /**
     * Deletes a system setting by its key.
     * Requires 'setting.edit' authority.
     *
     * @param key the unique key of the setting to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(
            @PathVariable String key) {
        
        settingService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
