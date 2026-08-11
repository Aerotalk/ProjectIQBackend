package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LeaveBalance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType"})
    List<LeaveBalance> findByEmployeeId(UUID employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType"})
    List<LeaveBalance> findByEmployeeIdAndYear(UUID employeeId, Integer year);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType"})
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(UUID employeeId, UUID leaveTypeId, Integer year);
}

