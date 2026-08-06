package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayslipTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayslipTemplateRepository extends JpaRepository<PayslipTemplate, UUID> {
    List<PayslipTemplate> findByOrganizationId(UUID organizationId);
}
