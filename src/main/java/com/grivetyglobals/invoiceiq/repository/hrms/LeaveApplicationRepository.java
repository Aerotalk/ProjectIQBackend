package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LeaveApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approver"})
    List<LeaveApplication> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approver"})
    List<LeaveApplication> findByEmployeeId(UUID employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approver"})
    List<LeaveApplication> findByOrganizationIdAndStatus(UUID organizationId, String status);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approver"})
    List<LeaveApplication> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approver"})
    List<LeaveApplication> findByEmployeeIdAndStatus(UUID employeeId, String status);
}

