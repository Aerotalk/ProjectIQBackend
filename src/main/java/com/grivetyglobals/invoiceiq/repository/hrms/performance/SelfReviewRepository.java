package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.SelfReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SelfReviewRepository extends JpaRepository<SelfReview, UUID> {
    List<SelfReview> findByOrganizationId(UUID organizationId);
    List<SelfReview> findByEmployeeId(UUID employeeId);
    Optional<SelfReview> findByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId);
}
