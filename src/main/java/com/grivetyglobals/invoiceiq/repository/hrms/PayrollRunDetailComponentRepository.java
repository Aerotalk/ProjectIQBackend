package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetailComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRunDetailComponentRepository extends JpaRepository<PayrollRunDetailComponent, UUID> {
    List<PayrollRunDetailComponent> findByPayrollRunDetailId(UUID payrollRunDetailId);
    void deleteByPayrollRunDetailId(UUID payrollRunDetailId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.amount), 0) FROM PayrollRunDetailComponent c WHERE c.payrollRunDetail.payrollRun.id = :runId AND c.componentName = 'Reimbursement'")
    java.math.BigDecimal getTotalReimbursementsByRunId(@org.springframework.data.repository.query.Param("runId") UUID runId);
}
