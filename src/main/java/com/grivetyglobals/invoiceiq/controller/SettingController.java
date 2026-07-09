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

    @PreAuthorize("hasAuthority('setting.view')")
    @GetMapping
    public ResponseEntity<List<Setting>> getSettings(
            @RequestParam(value = "category", required = false) String category) {
        
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(settingService.getSettingsByCategory(category));
        }
        return ResponseEntity.ok(settingService.getSettingsByOrganization());
    }

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

    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping
    public ResponseEntity<Setting> saveSetting(
            @RequestBody SettingRequest request) {
        
        return ResponseEntity.ok(settingService.saveSetting(request.getKey(), request.getValue(), request.getCategory()));
    }

    @PreAuthorize("hasAuthority('setting.edit')")
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(
            @PathVariable String key) {
        
        settingService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
