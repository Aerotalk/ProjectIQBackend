package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseClaim;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, UUID> {
    @EntityGraph(attributePaths = {"employee", "department", "project", "template", "currentReviewer"})
    List<ExpenseClaim> findByOrganizationId(UUID organizationId);
    
    @EntityGraph(attributePaths = {"employee", "department", "project", "template", "currentReviewer"})
    List<ExpenseClaim> findByEmployeeId(UUID employeeId);
    
    @EntityGraph(attributePaths = {"employee", "department", "project", "template", "currentReviewer"})
    List<ExpenseClaim> findByCurrentReviewerId(UUID currentReviewerId);
    
    @EntityGraph(attributePaths = {"employee", "department", "project", "template", "currentReviewer"})
    List<ExpenseClaim> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
