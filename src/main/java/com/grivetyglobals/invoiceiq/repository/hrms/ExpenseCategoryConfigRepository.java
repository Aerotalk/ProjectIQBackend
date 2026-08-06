package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseCategoryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseCategoryConfigRepository extends JpaRepository<ExpenseCategoryConfig, UUID> {
    List<ExpenseCategoryConfig> findByOrganizationId(UUID organizationId);
    List<ExpenseCategoryConfig> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
