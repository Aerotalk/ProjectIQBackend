package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.CalibrationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface CalibrationRecordRepository extends JpaRepository<CalibrationRecord, UUID> {
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    List<CalibrationRecord> findByOrganizationId(UUID organizationId);
    
    List<CalibrationRecord> findByCycleId(UUID cycleId);
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    List<CalibrationRecord> findByOrganizationIdAndCycleId(UUID organizationId, UUID cycleId);
    
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "cycle"})
    Optional<CalibrationRecord> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
