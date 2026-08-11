package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ProcessedAttendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessedAttendanceRepository extends JpaRepository<ProcessedAttendance, UUID> {
    @EntityGraph(attributePaths = {"employee", "period"})
    List<ProcessedAttendance> findByPeriodId(UUID periodId);
    
    void deleteByPeriodId(UUID periodId);
}
