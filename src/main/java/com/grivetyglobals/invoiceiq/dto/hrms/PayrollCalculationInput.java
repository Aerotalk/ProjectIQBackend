package com.grivetyglobals.invoiceiq.dto.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.EmployeeSalaryRevision;
import com.grivetyglobals.invoiceiq.entity.EmployeeStatutory;
import com.grivetyglobals.invoiceiq.entity.hrms.EmployeeLOP;
import com.grivetyglobals.invoiceiq.entity.hrms.ReimbursementClaim;
import com.grivetyglobals.invoiceiq.entity.hrms.SalaryHold;
import com.grivetyglobals.invoiceiq.entity.hrms.SalaryInput;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PayrollCalculationInput {
    private Employee employee;
    private EmployeeSalaryRevision salaryRevision;
    private EmployeeStatutory statutoryDetails;
    private List<SalaryInput> salaryInputs;
    private List<EmployeeLOP> lopDays;
    private List<ReimbursementClaim> reimbursements;
    private List<com.grivetyglobals.invoiceiq.entity.hrms.FBPDeclarationItem> fbpItems;
    private SalaryHold activeHold;
    private LocalDate payrollPeriodEnd;
}
