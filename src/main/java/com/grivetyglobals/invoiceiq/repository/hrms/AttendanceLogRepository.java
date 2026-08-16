package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<AttendanceLog> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<AttendanceLog> findByEmployeeId(UUID employeeId);
}
