package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.ManagerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManagerReviewRepository extends JpaRepository<ManagerReview, UUID> {
    List<ManagerReview> findByOrganizationId(UUID organizationId);
    List<ManagerReview> findByManagerId(UUID managerId);
    List<ManagerReview> findByEmployeeId(UUID employeeId);
    Optional<ManagerReview> findBySelfReviewId(UUID selfReviewId);
}
