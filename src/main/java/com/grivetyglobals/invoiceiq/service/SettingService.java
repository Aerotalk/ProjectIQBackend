package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.Setting;
import com.grivetyglobals.invoiceiq.repository.SettingRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final AuditService auditService;

    public List<Setting> getSettingsByOrganization() {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationId(currentOrgId);
    }

    public List<Setting> getSettingsByCategory(String category) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationIdAndCategory(currentOrgId, category);
    }

    public Setting getSettingByKey(String key) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return settingRepository.findByOrganizationIdAndSettingKey(currentOrgId, key).orElse(null);
    }

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
