package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseAdvanceRepository extends JpaRepository<ExpenseAdvance, UUID> {
    List<ExpenseAdvance> findByOrganizationId(UUID organizationId);
    List<ExpenseAdvance> findByEmployeeId(UUID employeeId);
}
