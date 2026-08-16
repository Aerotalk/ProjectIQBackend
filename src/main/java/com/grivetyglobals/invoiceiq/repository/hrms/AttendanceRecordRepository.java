package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByOrganizationIdAndStatus(UUID organizationId, String status);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByOrganizationIdAndAttendanceDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByOrganizationIdAndAttendanceDateBetweenAndStatus(UUID organizationId, LocalDate startDate, LocalDate endDate, String status);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    Optional<AttendanceRecord> findByOrganizationIdAndEmployeeIdAndAttendanceDate(UUID organizationId, UUID employeeId, LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.designation", "shift"})
    Optional<AttendanceRecord> findTopByOrganizationIdAndEmployeeIdOrderByAttendanceDateDesc(UUID organizationId, UUID employeeId);
}

