package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {
    List<AttendanceRecord> findByOrganizationId(UUID organizationId);
    List<AttendanceRecord> findByOrganizationIdAndAttendanceDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);
    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate attendanceDate);
}
