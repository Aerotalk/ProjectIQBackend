package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ShiftRoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRosterRepository extends JpaRepository<ShiftRoster, UUID> {
    List<ShiftRoster> findByOrganizationId(UUID organizationId);
    List<ShiftRoster> findByOrganizationIdAndRosterDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
    List<ShiftRoster> findByEmployeeIdAndRosterDate(UUID employeeId, LocalDate rosterDate);
}
