package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.SalaryStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryStopRepository extends JpaRepository<SalaryStop, UUID> {
    List<SalaryStop> findByOrganizationId(UUID organizationId);
    List<SalaryStop> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
    List<SalaryStop> findByEmployeeId(UUID employeeId);
}
