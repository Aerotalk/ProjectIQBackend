package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.ManagerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManagerReviewRepository extends JpaRepository<ManagerReview, UUID> {

    // Full graph (with rating collections) — for list/detail views
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "manager", "manager.department", "manager.designation", "selfReview", "cycle"})
    List<ManagerReview> findByOrganizationId(UUID organizationId);

    List<ManagerReview> findByManagerId(UUID managerId);
    List<ManagerReview> findByEmployeeId(UUID employeeId);

    // Full graph — for single-record detail/edit page
    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "manager", "manager.department", "manager.designation", "selfReview", "cycle"})
    Optional<ManagerReview> findBySelfReviewId(UUID selfReviewId);

    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "manager", "manager.department", "manager.designation", "selfReview", "cycle"})
    Optional<ManagerReview> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @EntityGraph(attributePaths = {"goalRatings", "goalRatings.goal", "competencyRatings", "competencyRatings.competency", "employee", "employee.department", "employee.designation", "manager", "manager.department", "manager.designation", "selfReview", "cycle"})
    List<ManagerReview> findByOrganizationIdAndCycleId(UUID organizationId, UUID cycleId);

    // Lightweight — used by dashboard & reports (only needs overallRating, employee, manager, cycle)
    // Excludes rating bags to avoid MultipleBagFetchException
    @EntityGraph(attributePaths = {"employee", "employee.department", "manager", "cycle"})
    List<ManagerReview> findByOrganizationIdAndStatus(UUID organizationId, String status);

    long countByOrganizationIdAndStatus(UUID organizationId, String status);
}
