package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseClaimTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseClaimTemplateRepository extends JpaRepository<ExpenseClaimTemplate, UUID> {
    List<ExpenseClaimTemplate> findByOrganizationId(UUID organizationId);
}
