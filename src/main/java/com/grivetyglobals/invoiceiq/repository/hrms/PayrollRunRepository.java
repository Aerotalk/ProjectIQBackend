package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, UUID> {
    List<PayrollRun> findByOrganizationId(UUID organizationId);
    Optional<PayrollRun> findByOrganizationIdAndPayrollPeriod(UUID organizationId, String payrollPeriod);
}
