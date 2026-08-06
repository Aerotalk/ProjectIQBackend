package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.CalibrationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalibrationRecordRepository extends JpaRepository<CalibrationRecord, UUID> {
    List<CalibrationRecord> findByOrganizationId(UUID organizationId);
    List<CalibrationRecord> findByCycleId(UUID cycleId);
}
