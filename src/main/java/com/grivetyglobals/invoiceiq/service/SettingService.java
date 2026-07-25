package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.Setting;
import com.grivetyglobals.invoiceiq.repository.SettingRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing application and organization-level settings.
 * Handles CRUD operations and logs setting changes for auditing.
 */
@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final AuditService auditService;

    /**
     * Retrieves all settings configured for the current organization.
     * 
     * @return a list of Setting entities
     */
    public List<Setting> getSettingsByOrganization() {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationId(currentOrgId);
    }

    /**
     * Retrieves settings for the current organization filtered by a specific category.
     * 
     * @param category the category string
     * @return a list of Setting entities matching the category
     */
    public List<Setting> getSettingsByCategory(String category) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationIdAndCategory(currentOrgId, category);
    }

    /**
     * Retrieves a specific setting by its key for the current organization.
     * 
     * @param key the setting key
     * @return the Setting entity, or null if not found
     */
    public Setting getSettingByKey(String key) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationIdAndSettingKey(currentOrgId, key).orElse(null);
    }

    /**
     * Saves or updates a setting. Logs the action using AuditService.
     * 
     * @param key      the setting key
     * @param value    the setting value
     * @param category the optional category
     * @return the saved Setting entity
     */
    @Transactional
    public Setting saveSetting(String key, String value, String category) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        
        Setting setting = settingRepository.findByOrganizationIdAndSettingKey(currentOrgId, key)
                .orElseGet(() -> Setting.builder()
                        .organizationId(currentOrgId)
                        .settingKey(key)
                        .build());
                        
        String oldValue = setting.getSettingValue();
        setting.setSettingValue(value);
        if (category != null) {
            setting.setCategory(category);
        }

        Setting saved = settingRepository.save(setting);

        // Audit the change
        String action = (oldValue == null) ? "SETTING_CREATED" : "SETTING_UPDATED";
        String description = String.format("Setting '%s' was %s", key, (oldValue == null) ? "created" : "updated");
        auditService.logActivity(action, description, saved.getId(), "Setting", currentUserId, currentOrgId);

        return saved;
    }

    /**
     * Deletes a setting by its key. Logs the action using AuditService.
     * 
     * @param key the setting key to delete
     */
    @Transactional
    public void deleteSetting(String key) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        
        settingRepository.findByOrganizationIdAndSettingKey(currentOrgId, key).ifPresent(setting -> {
            settingRepository.delete(setting);
            auditService.logActivity("SETTING_DELETED", "Setting '" + key + "' was deleted", setting.getId(), "Setting", currentUserId, currentOrgId);
        });
    }
}
