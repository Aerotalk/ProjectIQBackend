package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, UUID> {
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    List<PerformanceGoal> findByOrganizationId(UUID organizationId);
    
    List<PerformanceGoal> findByEmployeeId(UUID employeeId);
    List<PerformanceGoal> findByCycleId(UUID cycleId);
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    List<PerformanceGoal> findByOrganizationIdAndCycleId(UUID organizationId, UUID cycleId);
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    List<PerformanceGoal> findTop3ByOrganizationIdOrderByProgressDesc(UUID organizationId);
}
