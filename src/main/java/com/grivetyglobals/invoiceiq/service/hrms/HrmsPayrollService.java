package com.grivetyglobals.invoiceiq.service.hrms;

import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.EmployeeBankAccount;
import com.grivetyglobals.invoiceiq.entity.EmployeeSalaryRevision;
import com.grivetyglobals.invoiceiq.entity.EmployeeStatutory;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.repository.hrms.*;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.grivetyglobals.invoiceiq.dto.hrms.PayrollVarianceDto;

@Service
@RequiredArgsConstructor
public class HrmsPayrollService {

    private final PayComponentRepository payComponentRepository;
    private final PayslipTemplateRepository payslipTemplateRepository;
    private final SalaryInputRepository salaryInputRepository;
    private final EmployeeLOPRepository employeeLOPRepository;
    private final SalaryHoldRepository salaryHoldRepository;
    private final SalaryStopRepository salaryStopRepository;

    private final ITDeclarationRepository itDeclarationRepository;
    private final ITDeclarationItemRepository itDeclarationItemRepository;
    private final ReimbursementClaimRepository reimbursementClaimRepository;
    private final FBPDeclarationRepository fbpDeclarationRepository;
    private final FBPDeclarationItemRepository fbpDeclarationItemRepository;

    private final PayrollRunRepository payrollRunRepository;
    private final PayrollRunDetailRepository payrollRunDetailRepository;
    private final FinalSettlementRepository finalSettlementRepository;
    private final FinalSettlementItemRepository finalSettlementItemRepository;

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final com.grivetyglobals.invoiceiq.repository.EmployeeBankAccountRepository employeeBankAccountRepository;
    private final com.grivetyglobals.invoiceiq.repository.EmployeeStatutoryRepository employeeStatutoryRepository;
    private final com.grivetyglobals.invoiceiq.repository.EmployeeSalaryRevisionRepository employeeSalaryRevisionRepository;
    private final UserRepository userRepository;

    private Organization getCurrentOrganization() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    // ─────────────────────────────────────────────────────────
    // PAY COMPONENTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PayComponent> getPayComponents() {
        return payComponentRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public PayComponent createPayComponent(PayComponent component) {
        component.setOrganization(getCurrentOrganization());
        if (component.getActive() == null) component.setActive(true);
        return payComponentRepository.save(component);
    }

    @Transactional
    public PayComponent updatePayComponent(UUID id, PayComponent updated) {
        PayComponent comp = payComponentRepository.findById(id).orElseThrow(() -> new RuntimeException("Pay Component not found"));
        if (!comp.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        comp.setComponentName(updated.getComponentName());
        comp.setCode(updated.getCode());
        comp.setType(updated.getType());
        comp.setSubType(updated.getSubType());
        comp.setCalculationType(updated.getCalculationType());
        comp.setPercentageOf(updated.getPercentageOf());
        comp.setPercentageValue(updated.getPercentageValue());
        comp.setMaxLimit(updated.getMaxLimit());
        comp.setTaxable(updated.getTaxable());
        comp.setProRata(updated.getProRata());
        comp.setPartOfCTC(updated.getPartOfCTC());
        comp.setPartOfGross(updated.getPartOfGross());
        comp.setDisplayOrder(updated.getDisplayOrder());
        if (updated.getActive() != null) comp.setActive(updated.getActive());
        return payComponentRepository.save(comp);
    }

    @Transactional
    public void deletePayComponent(UUID id) {
        payComponentRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // PAYSLIP TEMPLATES
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PayslipTemplate> getPayslipTemplates() {
        return payslipTemplateRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public PayslipTemplate createPayslipTemplate(PayslipTemplate template) {
        template.setOrganization(getCurrentOrganization());
        return payslipTemplateRepository.save(template);
    }

    @Transactional
    public PayslipTemplate updatePayslipTemplate(UUID id, PayslipTemplate updated) {
        PayslipTemplate tmpl = payslipTemplateRepository.findById(id).orElseThrow(() -> new RuntimeException("Template not found"));
        if (!tmpl.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        tmpl.setTemplateName(updated.getTemplateName());
        tmpl.setLayoutHTML(updated.getLayoutHTML());
        tmpl.setPreviewImage(updated.getPreviewImage());
        tmpl.setSetAsDefault(updated.getSetAsDefault());
        return payslipTemplateRepository.save(tmpl);
    }

    @Transactional
    public void deletePayslipTemplate(UUID id) {
        payslipTemplateRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // SALARY INPUTS & LOP
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SalaryInput> getSalaryInputs(String period) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        if (period != null && !period.isBlank()) {
            return salaryInputRepository.findByOrganizationIdAndPayrollPeriod(orgId, period);
        }
        return salaryInputRepository.findByOrganizationId(orgId);
    }

    @Transactional
    public SalaryInput createSalaryInput(SalaryInput input) {
        input.setOrganization(getCurrentOrganization());
        return salaryInputRepository.save(input);
    }

    @Transactional
    public SalaryInput updateSalaryInput(UUID id, SalaryInput updated) {
        SalaryInput input = salaryInputRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Input not found"));
        if (!input.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        input.setPayComponent(updated.getPayComponent());
        input.setAmount(updated.getAmount());
        input.setInputType(updated.getInputType());
        input.setReason(updated.getReason());
        input.setRecurring(updated.getRecurring());
        input.setRecurringUntil(updated.getRecurringUntil());
        return salaryInputRepository.save(input);
    }

    @Transactional
    public void deleteSalaryInput(UUID id) {
        salaryInputRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmployeeLOP> getEmployeeLOPs(String period) {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        if (period != null && !period.isBlank()) {
            return employeeLOPRepository.findByOrganizationIdAndPayrollPeriod(orgId, period);
        }
        return employeeLOPRepository.findByOrganizationId(orgId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeLOP> getEmployeeLOPsByEmployee(UUID employeeId) {
        return employeeLOPRepository.findByOrganizationIdAndEmployeeId(SecurityUtils.getCurrentOrganizationId(), employeeId);
    }

    @Transactional
    public EmployeeLOP createEmployeeLOP(EmployeeLOP lop) {
        lop.setOrganization(getCurrentOrganization());
        return employeeLOPRepository.save(lop);
    }

    @Transactional
    public EmployeeLOP updateEmployeeLOP(UUID id, EmployeeLOP updated) {
        EmployeeLOP lop = employeeLOPRepository.findById(id).orElseThrow(() -> new RuntimeException("LOP record not found"));
        if (!lop.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        lop.setLopDays(updated.getLopDays());
        lop.setSource(updated.getSource());
        lop.setReason(updated.getReason());
        return employeeLOPRepository.save(lop);
    }

    @Transactional
    public void deleteEmployeeLOP(UUID id) {
        employeeLOPRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // SALARY HOLDS & STOPS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SalaryHold> getSalaryHolds() {
        return salaryHoldRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public SalaryHold createSalaryHold(SalaryHold hold) {
        hold.setOrganization(getCurrentOrganization());
        if (hold.getActive() == null) hold.setActive(true);
        return salaryHoldRepository.save(hold);
    }

    @Transactional
    public SalaryHold updateSalaryHold(UUID id, SalaryHold updated) {
        SalaryHold hold = salaryHoldRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Hold not found"));
        if (!hold.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        hold.setHoldAmount(updated.getHoldAmount());
        hold.setReason(updated.getReason());
        if (updated.getActive() != null) hold.setActive(updated.getActive());
        return salaryHoldRepository.save(hold);
    }

    @Transactional
    public void deleteSalaryHold(UUID id) {
        salaryHoldRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SalaryStop> getSalaryStops() {
        return salaryStopRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public SalaryStop createSalaryStop(SalaryStop stop) {
        stop.setOrganization(getCurrentOrganization());
        if (stop.getActive() == null) stop.setActive(true);
        return salaryStopRepository.save(stop);
    }

    @Transactional
    public SalaryStop updateSalaryStop(UUID id, SalaryStop updated) {
        SalaryStop stop = salaryStopRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Stop not found"));
        if (!stop.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        stop.setStopFromDate(updated.getStopFromDate());
        stop.setStopUntilDate(updated.getStopUntilDate());
        stop.setReason(updated.getReason());
        if (updated.getActive() != null) stop.setActive(updated.getActive());
        return salaryStopRepository.save(stop);
    }

    @Transactional
    public void deleteSalaryStop(UUID id) {
        salaryStopRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────
    // IT & FBP DECLARATIONS, REIMBURSEMENTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ITDeclaration> getITDeclarations() {
        return itDeclarationRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<ITDeclaration> getITDeclarationsByEmployee(UUID employeeId) {
        return itDeclarationRepository.findByOrganizationIdAndEmployeeId(SecurityUtils.getCurrentOrganizationId(), employeeId);
    }

    @Transactional
    public ITDeclaration createITDeclaration(ITDeclaration decl) {
        Organization org = getCurrentOrganization();
        decl.setOrganization(org);
        // Enforce uniqueness: one declaration per employee per financial year
        if (decl.getEmployee() != null && decl.getFinancialYear() != null) {
            itDeclarationRepository.findByOrganizationIdAndEmployeeIdAndFinancialYear(
                org.getId(), decl.getEmployee().getId(), decl.getFinancialYear()
            ).ifPresent(existing -> {
                throw new IllegalStateException("An IT Declaration already exists for this employee for FY " + decl.getFinancialYear());
            });
        }
        return itDeclarationRepository.save(decl);
    }

    @Transactional
    public ITDeclaration updateITDeclaration(UUID id, ITDeclaration updated) {
        ITDeclaration decl = itDeclarationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IT Declaration not found"));
        if (!decl.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        if (updated.getTaxRegime() != null) decl.setTaxRegime(updated.getTaxRegime());
        if (updated.getFinancialYear() != null) decl.setFinancialYear(updated.getFinancialYear());
        return itDeclarationRepository.save(decl);
    }

    @Transactional
    public void deleteITDeclaration(UUID id) {
        ITDeclaration decl = itDeclarationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("IT Declaration not found"));
        if (!decl.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        // Delete associated items first
        List<ITDeclarationItem> items = itDeclarationItemRepository.findByDeclarationId(id);
        itDeclarationItemRepository.deleteAll(items);
        itDeclarationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ITDeclarationItem> getITDeclarationItems(UUID declarationId) {
        return itDeclarationItemRepository.findByDeclarationId(declarationId);
    }

    @Transactional
    public ITDeclarationItem addITDeclarationItem(UUID declarationId, ITDeclarationItem item) {
        ITDeclaration decl = itDeclarationRepository.findById(declarationId).orElseThrow(() -> new RuntimeException("Declaration not found"));
        if (!decl.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        item.setDeclaration(decl);
        return itDeclarationItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<ReimbursementClaim> getReimbursementClaims() {
        return reimbursementClaimRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<ReimbursementClaim> getReimbursementClaimsByEmployee(UUID employeeId) {
        return reimbursementClaimRepository.findByOrganizationIdAndEmployeeId(SecurityUtils.getCurrentOrganizationId(), employeeId);
    }

    @Transactional
    public ReimbursementClaim createReimbursementClaim(ReimbursementClaim claim) {
        claim.setOrganization(getCurrentOrganization());
        if (claim.getStatus() == null) claim.setStatus("Pending");
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional
    public ReimbursementClaim updateReimbursementClaim(UUID id, ReimbursementClaim updated) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        if (!claim.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        if (updated.getReimbursementType() != null) claim.setReimbursementType(updated.getReimbursementType());
        if (updated.getClaimPeriod() != null) claim.setClaimPeriod(updated.getClaimPeriod());
        if (updated.getClaimedAmount() != null) claim.setClaimedAmount(updated.getClaimedAmount());
        if (updated.getBillDate() != null) claim.setBillDate(updated.getBillDate());
        if (updated.getBillNumber() != null) claim.setBillNumber(updated.getBillNumber());
        if (updated.getRemarks() != null) claim.setRemarks(updated.getRemarks());
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional
    public void deleteReimbursementClaim(UUID id) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        if (!claim.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        reimbursementClaimRepository.deleteById(id);
    }

    @Transactional
    public ReimbursementClaim approveReimbursementClaim(UUID id) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
        if (!claim.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        claim.setStatus("Approved");
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional
    public ReimbursementClaim rejectReimbursementClaim(UUID id) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
        if (!claim.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        claim.setStatus("Rejected");
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional(readOnly = true)
    public List<FBPDeclaration> getFBPDeclarations() {
        return fbpDeclarationRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public FBPDeclaration createFBPDeclaration(FBPDeclaration decl) {
        decl.setOrganization(getCurrentOrganization());
        return fbpDeclarationRepository.save(decl);
    }

    @Transactional(readOnly = true)
    public List<FBPDeclarationItem> getFBPDeclarationItems(UUID declarationId) {
        return fbpDeclarationItemRepository.findByDeclarationId(declarationId);
    }

    @Transactional
    public FBPDeclarationItem addFBPDeclarationItem(UUID declarationId, FBPDeclarationItem item) {
        FBPDeclaration decl = fbpDeclarationRepository.findById(declarationId).orElseThrow(() -> new RuntimeException("FBP Declaration not found"));
        if (!decl.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        item.setDeclaration(decl);
        return fbpDeclarationItemRepository.save(item);
    }

    // ─────────────────────────────────────────────────────────
    // PAYROLL RUNS & PROCESSING
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PayrollRun> getPayrollRuns() {
        return payrollRunRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getPayrollEligibilityCheck() {
        UUID orgId = SecurityUtils.getCurrentOrganizationId();
        List<Employee> allEmployees = employeeRepository.findByOrganizationId(orgId);

        List<EmployeeSalaryRevision> salaries = employeeSalaryRevisionRepository.findByOrganizationId(orgId);
        List<EmployeeBankAccount> banks = employeeBankAccountRepository.findByOrganizationId(orgId);
        List<EmployeeStatutory> statuaries = employeeStatutoryRepository.findByOrganizationId(orgId);

        java.util.Set<UUID> salEmpIds = salaries.stream().map(s -> s.getEmployee().getId()).collect(java.util.stream.Collectors.toSet());
        java.util.Set<UUID> bankEmpIds = banks.stream().map(b -> b.getEmployee().getId()).collect(java.util.stream.Collectors.toSet());
        java.util.Set<UUID> statEmpIds = statuaries.stream().map(s -> s.getEmployee().getId()).collect(java.util.stream.Collectors.toSet());

        List<java.util.Map<String, Object>> missing = new java.util.ArrayList<>();
        for(Employee e : allEmployees) {
            List<String> missingSteps = new java.util.ArrayList<>();
            if(!salEmpIds.contains(e.getId())) missingSteps.add("Salary Configuration");
            if(!bankEmpIds.contains(e.getId())) missingSteps.add("Bank Details");
            if(!statEmpIds.contains(e.getId())) missingSteps.add("Statutory Details");

            if(!missingSteps.isEmpty()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", e.getId());
                map.put("name", e.getFirstName() + " " + e.getLastName());
                map.put("empId", e.getEmployeeCode());
                map.put("missingSteps", missingSteps);
                missing.add(map);
            }
        }
        return missing;
    }

    @Transactional(readOnly = true)
    public PayrollRun getPayrollRunById(UUID id) {
        var payrollRun = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        if (!payrollRun.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        return payrollRun;
    }

    @Transactional
    public PayrollRun createPayrollRun(PayrollRun run) {
        run.setOrganization(getCurrentOrganization());
        if (run.getStatus() == null) run.setStatus("Draft");
        if (run.getPayoutStatus() == null) run.setPayoutStatus("Unpaid");
        return payrollRunRepository.save(run);
    }

    @Transactional
    public PayrollRun processPayrollRun(UUID id) {
        PayrollRun run = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        if (!run.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        
        // 1. Delete old details if re-processing
        List<PayrollRunDetail> oldDetails = payrollRunDetailRepository.findByPayrollRunId(id);
        if (!oldDetails.isEmpty()) {
            payrollRunDetailRepository.deleteAll(oldDetails);
        }

        // 2. Fetch all employees for this organization
        List<Employee> employees = employeeRepository.searchAndFilterEmployees(run.getOrganization().getId(), null, null, "Active", null, null);
        
        if (run.getEmployeeScope() != null && run.getEmployeeScope().equals("Department") && run.getDepartment() != null) {
            employees = employees.stream()
                .filter(e -> e.getDepartment() != null && e.getDepartment().getId().equals(run.getDepartment().getId()))
                .toList();
        }

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        
        List<SalaryInput> allInputs = salaryInputRepository.findByOrganizationIdAndPayrollPeriod(run.getOrganization().getId(), run.getPayrollPeriod());
        List<EmployeeLOP> allLops = employeeLOPRepository.findByOrganizationIdAndPayrollPeriod(run.getOrganization().getId(), run.getPayrollPeriod());

        for (Employee emp : employees) {
            BigDecimal baseSalary = BigDecimal.valueOf(50000.0);
            
            List<SalaryInput> inputs = allInputs.stream().filter(s -> s.getEmployee().getId().equals(emp.getId())).toList();
            BigDecimal additions = BigDecimal.ZERO;
            BigDecimal deductions = BigDecimal.ZERO;
            
            for(SalaryInput input : inputs) {
                if("Addition".equalsIgnoreCase(input.getInputType())) {
                    additions = additions.add(input.getAmount());
                } else if("Deduction".equalsIgnoreCase(input.getInputType())) {
                    deductions = deductions.add(input.getAmount());
                } else if("Override".equalsIgnoreCase(input.getInputType())) {
                    baseSalary = input.getAmount();
                }
            }
            
            List<EmployeeLOP> lops = allLops.stream().filter(l -> l.getEmployee().getId().equals(emp.getId())).toList();
            BigDecimal totalLopDays = BigDecimal.ZERO;
            for(EmployeeLOP lop : lops) {
                totalLopDays = totalLopDays.add(lop.getLopDays());
            }
            
            BigDecimal lopDeduction = baseSalary.divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP).multiply(totalLopDays);
            deductions = deductions.add(lopDeduction);
            
            BigDecimal tds = baseSalary.multiply(BigDecimal.valueOf(0.10));
            deductions = deductions.add(tds);
            
            BigDecimal gross = baseSalary.add(additions);
            BigDecimal net = gross.subtract(deductions);
            
            PayrollRunDetail detail = new PayrollRunDetail();
            detail.setPayrollRun(run);
            detail.setEmployee(emp);
            detail.setGross(gross);
            detail.setTotalDeductions(deductions);
            detail.setNet(net);
            detail.setLopDays(totalLopDays);
            detail.setPayableDays(BigDecimal.valueOf(30).subtract(totalLopDays));
            
            payrollRunDetailRepository.save(detail);
            
            totalGross = totalGross.add(gross);
            totalDeductions = totalDeductions.add(deductions);
            totalNet = totalNet.add(net);
        }

        run.setStatus("Processed");
        run.setProcessedOn(LocalDateTime.now());
        run.setEmployeeCount(employees.size());
        run.setTotalGross(totalGross);
        run.setTotalDeductions(totalDeductions);
        run.setTotalNet(totalNet);
        return payrollRunRepository.save(run);
    }
    
    @Transactional(readOnly = true)
    public List<PayrollVarianceDto> getPayrollVariances(UUID runId) {
        PayrollRun run = payrollRunRepository.findById(runId).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        if (!run.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        
        List<PayrollVarianceDto> variances = new ArrayList<>();
        // Compare with dummy data for immediate integration (as "previous" requires complex historical querying)
        variances.add(new PayrollVarianceDto("Basic Pay", "₹" + run.getTotalGross().subtract(BigDecimal.valueOf(25000)), "₹" + run.getTotalGross(), "+₹25,000", "Salary Revisions/Additions"));
        variances.add(new PayrollVarianceDto("Deductions", "₹" + run.getTotalDeductions().subtract(BigDecimal.valueOf(4000)), "₹" + run.getTotalDeductions(), "+₹4,000", "Higher LOP/TDS"));
        variances.add(new PayrollVarianceDto("Net Pay", "₹" + run.getTotalNet().subtract(BigDecimal.valueOf(21000)), "₹" + run.getTotalNet(), "+₹21,000", "Net Change"));
        
        return variances;
    }

    @Transactional
    public PayrollRun approvePayrollRun(UUID id) {
        PayrollRun run = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        if (!run.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        run.setStatus("Approved");
        run.setApprovedOn(LocalDateTime.now());
        return payrollRunRepository.save(run);
    }

    @Transactional
    public PayrollRun updatePayoutStatus(UUID id, String status) {
        PayrollRun run = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        if (!run.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        run.setPayoutStatus(status);
        return payrollRunRepository.save(run);
    }

    @Transactional(readOnly = true)
    public List<PayrollRunDetail> getPayrollRunDetails(UUID runId) {
        return payrollRunDetailRepository.findByPayrollRunId(runId);
    }

    // ─────────────────────────────────────────────────────────
    // FINAL SETTLEMENTS
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FinalSettlement> getFinalSettlements() {
        return finalSettlementRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public FinalSettlement createFinalSettlement(FinalSettlement settlement) {
        settlement.setOrganization(getCurrentOrganization());
        if (settlement.getStatus() == null) settlement.setStatus("Draft");
        return finalSettlementRepository.save(settlement);
    }

    @Transactional
    public FinalSettlement processFinalSettlement(UUID id) {
        FinalSettlement settlement = finalSettlementRepository.findById(id).orElseThrow(() -> new RuntimeException("Settlement not found"));
        if (!settlement.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        settlement.setStatus("Processed");
        return finalSettlementRepository.save(settlement);
    }

    @Transactional(readOnly = true)
    public List<FinalSettlementItem> getFinalSettlementItems(UUID settlementId) {
        return finalSettlementItemRepository.findBySettlementId(settlementId);
    }

    @Transactional
    public FinalSettlementItem addFinalSettlementItem(UUID settlementId, FinalSettlementItem item) {
        FinalSettlement settlement = finalSettlementRepository.findById(settlementId).orElseThrow(() -> new RuntimeException("Settlement not found"));
        if (!settlement.getOrganization().getId().equals(SecurityUtils.getCurrentOrganizationId())) {
            throw new SecurityException("Access Denied");
        }
        item.setSettlement(settlement);
        return finalSettlementItemRepository.save(item);
    }
}
