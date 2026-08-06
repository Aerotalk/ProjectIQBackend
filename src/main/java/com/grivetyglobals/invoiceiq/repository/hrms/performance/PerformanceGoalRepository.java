package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, UUID> {
    List<PerformanceGoal> findByOrganizationId(UUID organizationId);
    List<PerformanceGoal> findByEmployeeId(UUID employeeId);
    List<PerformanceGoal> findByCycleId(UUID cycleId);
}
