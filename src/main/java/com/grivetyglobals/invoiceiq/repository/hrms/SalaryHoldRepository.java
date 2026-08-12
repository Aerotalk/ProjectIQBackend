package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.SalaryHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryHoldRepository extends JpaRepository<SalaryHold, UUID> {
    List<SalaryHold> findByOrganizationId(UUID organizationId);
    List<SalaryHold> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
    List<SalaryHold> findByEmployeeId(UUID employeeId);
}
