package com.grivetyglobals.invoiceiq.controller.hrms;

import com.grivetyglobals.invoiceiq.dto.hrms.PayrollVarianceDto;
import com.grivetyglobals.invoiceiq.entity.hrms.*;
import com.grivetyglobals.invoiceiq.service.hrms.HrmsPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hrms/payroll")
@RequiredArgsConstructor
public class HrmsPayrollController {

    private final HrmsPayrollService hrmsPayrollService;

    // ─────────────────────────────────────────────────────────
    // PAY COMPONENTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pay-components")
    public ResponseEntity<List<PayComponent>> getPayComponents() {
        return ResponseEntity.ok(hrmsPayrollService.getPayComponents());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/pay-components")
    public ResponseEntity<PayComponent> createPayComponent(@RequestBody PayComponent component) {
        return ResponseEntity.ok(hrmsPayrollService.createPayComponent(component));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/pay-components/{id}")
    public ResponseEntity<PayComponent> updatePayComponent(@PathVariable UUID id, @RequestBody PayComponent component) {
        return ResponseEntity.ok(hrmsPayrollService.updatePayComponent(id, component));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/pay-components/{id}")
    public ResponseEntity<Void> deletePayComponent(@PathVariable UUID id) {
        hrmsPayrollService.deletePayComponent(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // PAYSLIP TEMPLATES
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payslip-templates")
    public ResponseEntity<List<PayslipTemplate>> getPayslipTemplates() {
        return ResponseEntity.ok(hrmsPayrollService.getPayslipTemplates());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payslip-templates")
    public ResponseEntity<PayslipTemplate> createPayslipTemplate(@RequestBody PayslipTemplate template) {
        return ResponseEntity.ok(hrmsPayrollService.createPayslipTemplate(template));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/payslip-templates/{id}")
    public ResponseEntity<PayslipTemplate> updatePayslipTemplate(@PathVariable UUID id, @RequestBody PayslipTemplate template) {
        return ResponseEntity.ok(hrmsPayrollService.updatePayslipTemplate(id, template));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/payslip-templates/{id}")
    public ResponseEntity<Void> deletePayslipTemplate(@PathVariable UUID id) {
        hrmsPayrollService.deletePayslipTemplate(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // SALARY INPUTS & LOP
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/salary-inputs")
    public ResponseEntity<List<SalaryInput>> getSalaryInputs(@RequestParam(required = false) String period) {
        return ResponseEntity.ok(hrmsPayrollService.getSalaryInputs(period));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/salary-inputs")
    public ResponseEntity<SalaryInput> createSalaryInput(@RequestBody SalaryInput input) {
        return ResponseEntity.ok(hrmsPayrollService.createSalaryInput(input));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/salary-inputs/{id}")
    public ResponseEntity<SalaryInput> updateSalaryInput(@PathVariable UUID id, @RequestBody SalaryInput input) {
        return ResponseEntity.ok(hrmsPayrollService.updateSalaryInput(id, input));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/salary-inputs/{id}")
    public ResponseEntity<Void> deleteSalaryInput(@PathVariable UUID id) {
        hrmsPayrollService.deleteSalaryInput(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/employee-lop")
    public ResponseEntity<List<EmployeeLOP>> getEmployeeLOPs(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) UUID employeeId) {
        if (employeeId != null) {
            return ResponseEntity.ok(hrmsPayrollService.getEmployeeLOPsByEmployee(employeeId));
        }
        return ResponseEntity.ok(hrmsPayrollService.getEmployeeLOPs(period));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/employee-lop")
    public ResponseEntity<EmployeeLOP> createEmployeeLOP(@RequestBody EmployeeLOP lop) {
        return ResponseEntity.ok(hrmsPayrollService.createEmployeeLOP(lop));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/employee-lop/{id}")
    public ResponseEntity<EmployeeLOP> updateEmployeeLOP(@PathVariable UUID id, @RequestBody EmployeeLOP lop) {
        return ResponseEntity.ok(hrmsPayrollService.updateEmployeeLOP(id, lop));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/employee-lop/{id}")
    public ResponseEntity<Void> deleteEmployeeLOP(@PathVariable UUID id) {
        hrmsPayrollService.deleteEmployeeLOP(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // SALARY HOLDS & STOPS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/salary-holds")
    public ResponseEntity<List<SalaryHold>> getSalaryHolds() {
        return ResponseEntity.ok(hrmsPayrollService.getSalaryHolds());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/salary-holds")
    public ResponseEntity<SalaryHold> createSalaryHold(@RequestBody SalaryHold hold) {
        return ResponseEntity.ok(hrmsPayrollService.createSalaryHold(hold));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/salary-holds/{id}")
    public ResponseEntity<SalaryHold> updateSalaryHold(@PathVariable UUID id, @RequestBody SalaryHold hold) {
        return ResponseEntity.ok(hrmsPayrollService.updateSalaryHold(id, hold));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/salary-holds/{id}")
    public ResponseEntity<Void> deleteSalaryHold(@PathVariable UUID id) {
        hrmsPayrollService.deleteSalaryHold(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/salary-stops")
    public ResponseEntity<List<SalaryStop>> getSalaryStops() {
        return ResponseEntity.ok(hrmsPayrollService.getSalaryStops());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/salary-stops")
    public ResponseEntity<SalaryStop> createSalaryStop(@RequestBody SalaryStop stop) {
        return ResponseEntity.ok(hrmsPayrollService.createSalaryStop(stop));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/salary-stops/{id}")
    public ResponseEntity<SalaryStop> updateSalaryStop(@PathVariable UUID id, @RequestBody SalaryStop stop) {
        return ResponseEntity.ok(hrmsPayrollService.updateSalaryStop(id, stop));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/salary-stops/{id}")
    public ResponseEntity<Void> deleteSalaryStop(@PathVariable UUID id) {
        hrmsPayrollService.deleteSalaryStop(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // IT & FBP DECLARATIONS, REIMBURSEMENTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/it-declarations")
    public ResponseEntity<List<ITDeclaration>> getITDeclarations(@RequestParam(required = false) UUID employeeId) {
        if (employeeId != null) {
            return ResponseEntity.ok(hrmsPayrollService.getITDeclarationsByEmployee(employeeId));
        }
        return ResponseEntity.ok(hrmsPayrollService.getITDeclarations());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/it-declarations")
    public ResponseEntity<ITDeclaration> createITDeclaration(@RequestBody ITDeclaration decl) {
        return ResponseEntity.ok(hrmsPayrollService.createITDeclaration(decl));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/it-declarations/{id}")
    public ResponseEntity<ITDeclaration> updateITDeclaration(@PathVariable UUID id, @RequestBody ITDeclaration decl) {
        return ResponseEntity.ok(hrmsPayrollService.updateITDeclaration(id, decl));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/it-declarations/{id}")
    public ResponseEntity<Void> deleteITDeclaration(@PathVariable UUID id) {
        hrmsPayrollService.deleteITDeclaration(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/it-declarations/{id}/items")
    public ResponseEntity<List<ITDeclarationItem>> getITDeclarationItems(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getITDeclarationItems(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/it-declarations/{id}/items")
    public ResponseEntity<ITDeclarationItem> addITDeclarationItem(@PathVariable UUID id, @RequestBody ITDeclarationItem item) {
        return ResponseEntity.ok(hrmsPayrollService.addITDeclarationItem(id, item));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reimbursements")
    public ResponseEntity<List<ReimbursementClaim>> getReimbursementClaims(@RequestParam(required = false) UUID employeeId) {
        if (employeeId != null) {
            return ResponseEntity.ok(hrmsPayrollService.getReimbursementClaimsByEmployee(employeeId));
        }
        return ResponseEntity.ok(hrmsPayrollService.getReimbursementClaims());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reimbursements")
    public ResponseEntity<ReimbursementClaim> createReimbursementClaim(@RequestBody ReimbursementClaim claim) {
        return ResponseEntity.ok(hrmsPayrollService.createReimbursementClaim(claim));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/reimbursements/{id}")
    public ResponseEntity<ReimbursementClaim> updateReimbursementClaim(@PathVariable UUID id, @RequestBody ReimbursementClaim claim) {
        return ResponseEntity.ok(hrmsPayrollService.updateReimbursementClaim(id, claim));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/reimbursements/{id}")
    public ResponseEntity<Void> deleteReimbursementClaim(@PathVariable UUID id) {
        hrmsPayrollService.deleteReimbursementClaim(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/reimbursements/{id}/approve")
    public ResponseEntity<ReimbursementClaim> approveReimbursementClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.approveReimbursementClaim(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/reimbursements/{id}/reject")
    public ResponseEntity<ReimbursementClaim> rejectReimbursementClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.rejectReimbursementClaim(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fbp-declarations")
    public ResponseEntity<List<FBPDeclaration>> getFBPDeclarations() {
        return ResponseEntity.ok(hrmsPayrollService.getFBPDeclarations());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fbp-declarations")
    public ResponseEntity<FBPDeclaration> createFBPDeclaration(@RequestBody FBPDeclaration decl) {
        return ResponseEntity.ok(hrmsPayrollService.createFBPDeclaration(decl));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fbp-declarations/{id}/items")
    public ResponseEntity<List<FBPDeclarationItem>> getFBPDeclarationItems(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getFBPDeclarationItems(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fbp-declarations/{id}/items")
    public ResponseEntity<FBPDeclarationItem> addFBPDeclarationItem(@PathVariable UUID id, @RequestBody FBPDeclarationItem item) {
        return ResponseEntity.ok(hrmsPayrollService.addFBPDeclarationItem(id, item));
    }

    // ─────────────────────────────────────────────────────────
    // PAYROLL RUNS & PROCESSING
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/eligibility-check")
    public ResponseEntity<List<Map<String, Object>>> getPayrollEligibilityCheck() {
        return ResponseEntity.ok(hrmsPayrollService.getPayrollEligibilityCheck());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/runs")
    public ResponseEntity<List<PayrollRun>> getPayrollRuns() {
        return ResponseEntity.ok(hrmsPayrollService.getPayrollRuns());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/runs/{id}")
    public ResponseEntity<PayrollRun> getPayrollRunById(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getPayrollRunById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/runs")
    public ResponseEntity<PayrollRun> createPayrollRun(@RequestBody PayrollRun run) {
        return ResponseEntity.ok(hrmsPayrollService.createPayrollRun(run));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/runs/{id}/process")
    public ResponseEntity<PayrollRun> processPayrollRun(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.processPayrollRun(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/runs/{id}/approve")
    public ResponseEntity<PayrollRun> approvePayrollRun(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.approvePayrollRun(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/runs/{id}/payout")
    public ResponseEntity<PayrollRun> updatePayoutStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("payoutStatus");
        return ResponseEntity.ok(hrmsPayrollService.updatePayoutStatus(id, status));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/runs/{id}/details")
    public ResponseEntity<List<PayrollRunDetail>> getPayrollRunDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getPayrollRunDetails(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/runs/{id}/variances")
    public ResponseEntity<List<PayrollVarianceDto>> getPayrollVariances(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getPayrollVariances(id));
    }

    // ─────────────────────────────────────────────────────────
    // FINAL SETTLEMENTS
    // ─────────────────────────────────────────────────────────

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/final-settlements")
    public ResponseEntity<List<FinalSettlement>> getFinalSettlements() {
        return ResponseEntity.ok(hrmsPayrollService.getFinalSettlements());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/final-settlements")
    public ResponseEntity<FinalSettlement> createFinalSettlement(@RequestBody FinalSettlement settlement) {
        return ResponseEntity.ok(hrmsPayrollService.createFinalSettlement(settlement));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/final-settlements/{id}/process")
    public ResponseEntity<FinalSettlement> processFinalSettlement(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.processFinalSettlement(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/final-settlements/{id}/items")
    public ResponseEntity<List<FinalSettlementItem>> getFinalSettlementItems(@PathVariable UUID id) {
        return ResponseEntity.ok(hrmsPayrollService.getFinalSettlementItems(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/final-settlements/{id}/items")
    public ResponseEntity<FinalSettlementItem> addFinalSettlementItem(@PathVariable UUID id, @RequestBody FinalSettlementItem item) {
        return ResponseEntity.ok(hrmsPayrollService.addFinalSettlementItem(id, item));
    }
}
