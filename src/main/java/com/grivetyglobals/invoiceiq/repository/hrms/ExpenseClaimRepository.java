package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, UUID> {
    List<ExpenseClaim> findByOrganizationId(UUID organizationId);
    List<ExpenseClaim> findByEmployeeId(UUID employeeId);
    List<ExpenseClaim> findByCurrentReviewerId(UUID currentReviewerId);
    List<ExpenseClaim> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
