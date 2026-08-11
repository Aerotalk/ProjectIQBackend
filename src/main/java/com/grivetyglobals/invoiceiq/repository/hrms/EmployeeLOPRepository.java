package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.EmployeeLOP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeLOPRepository extends JpaRepository<EmployeeLOP, UUID> {
    List<EmployeeLOP> findByOrganizationId(UUID organizationId);
    List<EmployeeLOP> findByOrganizationIdAndPayrollPeriod(UUID organizationId, String payrollPeriod);
    List<EmployeeLOP> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
}
