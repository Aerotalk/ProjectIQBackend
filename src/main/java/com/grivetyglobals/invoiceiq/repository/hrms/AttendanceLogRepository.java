package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<AttendanceLog> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<AttendanceLog> findByEmployeeId(UUID employeeId);

    List<AttendanceLog> findByOrganizationIdAndEmployeeIdAndTimestampBetweenOrderByTimestampAsc(
            UUID organizationId, UUID employeeId, java.time.LocalDateTime from, java.time.LocalDateTime to
    );

    Optional<AttendanceLog> findTopByOrganizationIdAndEmployeeIdOrderByTimestampDesc(
            UUID organizationId, UUID employeeId
    );
}
