package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseAuditLogRepository extends JpaRepository<ExpenseAuditLog, UUID> {
    List<ExpenseAuditLog> findByOrganizationId(UUID organizationId);
    List<ExpenseAuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);
}
