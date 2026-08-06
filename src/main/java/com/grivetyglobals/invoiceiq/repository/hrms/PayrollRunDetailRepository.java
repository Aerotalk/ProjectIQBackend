package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRunDetailRepository extends JpaRepository<PayrollRunDetail, UUID> {
    List<PayrollRunDetail> findByPayrollRunId(UUID payrollRunId);
    void deleteByPayrollRunId(UUID payrollRunId);
}
