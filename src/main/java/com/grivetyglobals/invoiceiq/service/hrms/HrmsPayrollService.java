package com.grivetyglobals.invoiceiq.service.hrms;

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
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public EmployeeLOP createEmployeeLOP(EmployeeLOP lop) {
        lop.setOrganization(getCurrentOrganization());
        return employeeLOPRepository.save(lop);
    }

    @Transactional
    public EmployeeLOP updateEmployeeLOP(UUID id, EmployeeLOP updated) {
        EmployeeLOP lop = employeeLOPRepository.findById(id).orElseThrow(() -> new RuntimeException("LOP record not found"));
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

    @Transactional
    public ITDeclaration createITDeclaration(ITDeclaration decl) {
        decl.setOrganization(getCurrentOrganization());
        return itDeclarationRepository.save(decl);
    }

    @Transactional(readOnly = true)
    public List<ITDeclarationItem> getITDeclarationItems(UUID declarationId) {
        return itDeclarationItemRepository.findByDeclarationId(declarationId);
    }

    @Transactional
    public ITDeclarationItem addITDeclarationItem(UUID declarationId, ITDeclarationItem item) {
        ITDeclaration decl = itDeclarationRepository.findById(declarationId).orElseThrow(() -> new RuntimeException("Declaration not found"));
        item.setDeclaration(decl);
        return itDeclarationItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<ReimbursementClaim> getReimbursementClaims() {
        return reimbursementClaimRepository.findByOrganizationId(SecurityUtils.getCurrentOrganizationId());
    }

    @Transactional
    public ReimbursementClaim createReimbursementClaim(ReimbursementClaim claim) {
        claim.setOrganization(getCurrentOrganization());
        if (claim.getStatus() == null) claim.setStatus("Pending");
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional
    public ReimbursementClaim approveReimbursementClaim(UUID id) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setStatus("Approved");
        return reimbursementClaimRepository.save(claim);
    }

    @Transactional
    public ReimbursementClaim rejectReimbursementClaim(UUID id) {
        ReimbursementClaim claim = reimbursementClaimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
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
        run.setStatus("Processed");
        run.setProcessedOn(LocalDateTime.now());
        return payrollRunRepository.save(run);
    }

    @Transactional
    public PayrollRun approvePayrollRun(UUID id) {
        PayrollRun run = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
        run.setStatus("Approved");
        run.setApprovedOn(LocalDateTime.now());
        return payrollRunRepository.save(run);
    }

    @Transactional
    public PayrollRun updatePayoutStatus(UUID id, String status) {
        PayrollRun run = payrollRunRepository.findById(id).orElseThrow(() -> new RuntimeException("Payroll Run not found"));
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
        item.setSettlement(settlement);
        return finalSettlementItemRepository.save(item);
    }
}
