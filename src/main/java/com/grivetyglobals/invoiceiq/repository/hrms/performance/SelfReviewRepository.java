package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.SelfReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SelfReviewRepository extends JpaRepository<SelfReview, UUID> {
    
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "cycle"})
    List<SelfReview> findByOrganizationId(UUID organizationId);
    
    List<SelfReview> findByEmployeeId(UUID employeeId);
    
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "cycle"})
    Optional<SelfReview> findByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId);
    
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "cycle"})
    Optional<SelfReview> findByIdAndOrganizationId(UUID id, UUID organizationId);
    
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "cycle"})
    List<SelfReview> findByOrganizationIdAndCycleId(UUID organizationId, UUID cycleId);
    
    long countByOrganizationIdAndStatus(UUID organizationId, String status);
}
