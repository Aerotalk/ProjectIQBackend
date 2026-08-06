package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LockConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LockConfigurationRepository extends JpaRepository<LockConfiguration, UUID> {
    List<LockConfiguration> findByOrganizationId(UUID organizationId);
}
