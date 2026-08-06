package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendancePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendancePeriodRepository extends JpaRepository<AttendancePeriod, UUID> {
    List<AttendancePeriod> findByOrganizationId(UUID organizationId);
    Optional<AttendancePeriod> findByOrganizationIdAndMonthAndYear(UUID organizationId, Integer month, Integer year);
}
