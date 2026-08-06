package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.SalaryInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryInputRepository extends JpaRepository<SalaryInput, UUID> {
    List<SalaryInput> findByOrganizationId(UUID organizationId);
    List<SalaryInput> findByOrganizationIdAndPayrollPeriod(UUID organizationId, String payrollPeriod);
    List<SalaryInput> findByEmployeeId(UUID employeeId);
}
