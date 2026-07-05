package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.Setting;
import com.grivetyglobals.invoiceiq.repository.SettingRepository;
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

    public List<Setting> getSettingsByOrganization(UUID organizationId) {
        return settingRepository.findByOrganizationId(organizationId);
    }

    public List<Setting> getSettingsByCategory(UUID organizationId, String category) {
        return settingRepository.findByOrganizationIdAndCategory(organizationId, category);
    }

    public Setting getSettingByKey(UUID organizationId, String key) {
        return settingRepository.findByOrganizationIdAndSettingKey(organizationId, key).orElse(null);
    }

    @Transactional
    public Setting saveSetting(UUID organizationId, String key, String value, String category, UUID userId) {
        Setting setting = settingRepository.findByOrganizationIdAndSettingKey(organizationId, key)
                .orElseGet(() -> Setting.builder()
                        .organizationId(organizationId)
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
        auditService.logActivity(action, description, saved.getId(), "Setting", userId, organizationId);

        return saved;
    }

    @Transactional
    public void deleteSetting(UUID organizationId, String key, UUID userId) {
        settingRepository.findByOrganizationIdAndSettingKey(organizationId, key).ifPresent(setting -> {
            settingRepository.delete(setting);
            auditService.logActivity("SETTING_DELETED", "Setting '" + key + "' was deleted", setting.getId(), "Setting", userId, organizationId);
        });
    }
}
