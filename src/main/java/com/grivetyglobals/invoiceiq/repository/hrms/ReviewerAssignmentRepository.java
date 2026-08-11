package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ReviewerAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewerAssignmentRepository extends JpaRepository<ReviewerAssignment, UUID> {
    @EntityGraph(attributePaths = {"employee", "template", "reviewer1", "reviewer2", "reviewer3"})
    List<ReviewerAssignment> findByOrganizationId(UUID organizationId);
    
    @EntityGraph(attributePaths = {"employee", "template", "reviewer1", "reviewer2", "reviewer3"})
    Optional<ReviewerAssignment> findByEmployeeIdAndTemplateId(UUID employeeId, UUID templateId);
}
