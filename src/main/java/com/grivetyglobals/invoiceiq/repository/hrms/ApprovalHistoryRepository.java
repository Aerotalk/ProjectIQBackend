package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, UUID> {
    List<ApprovalHistory> findByOrganizationId(UUID organizationId);
    List<ApprovalHistory> findByReferenceId(UUID referenceId);
}
