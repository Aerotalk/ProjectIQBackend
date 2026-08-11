package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ShiftRoster;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRosterRepository extends JpaRepository<ShiftRoster, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department", "assignedShift"})
    List<ShiftRoster> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "assignedShift"})
    List<ShiftRoster> findByOrganizationIdAndRosterDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "assignedShift"})
    List<ShiftRoster> findByEmployeeIdAndRosterDate(UUID employeeId, LocalDate rosterDate);
}
