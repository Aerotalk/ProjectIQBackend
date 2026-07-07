package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {
    List<Setting> findByOrganizationId(UUID organizationId);
    List<Setting> findByOrganizationIdAndCategory(UUID organizationId, String category);
    Optional<Setting> findByOrganizationIdAndSettingKey(UUID organizationId, String settingKey);
}
