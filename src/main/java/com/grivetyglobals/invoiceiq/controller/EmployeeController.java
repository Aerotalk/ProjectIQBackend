package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.EmployeeCreateRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeUpdateRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeAddressRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeEmergencyContactRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeStatutoryRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeBankAccountRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeDocumentRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeSalaryRevisionRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeEducationRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeFamilyRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeContractRequest;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.service.EmployeeService;
import com.grivetyglobals.invoiceiq.service.EmployeeDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for employee management.
 * Handles employee profiles, search, filtering, and status updates.
 */
@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeDetailService employeeDetailService;

    /**
     * Retrieves the current authenticated user's employee profile.
     *
     * @param principal the authenticated principal (user email)
     * @return the Employee entity
     */
    @PreAuthorize("hasAuthority('employee.view')")
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(Principal principal) {
        return ResponseEntity.ok(employeeService.getMyProfile(principal.getName()));
    }

    /**
     * Creates a new employee record.
     * Requires 'employee.create' authority.
     *
     * @param request the employee creation payload
     * @return the created Employee entity
     */
    @PreAuthorize("hasAuthority('employee.create')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    /**
     * Searches and filters all employees based on provided criteria.
     * Requires 'employee.view' authority.
     *
     * @param departmentId filter by department (optional)
     * @param status       filter by employment status (optional)
     * @param keyword      search by keyword (optional)
     * @return a list of matching Employee entities
     */
    @PreAuthorize("hasAuthority('employee.view')")
    @GetMapping
    public ResponseEntity<List<Employee>> searchAndFilterEmployees(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleName) {
        return ResponseEntity.ok(employeeService.searchAndFilterEmployees(departmentId, status, keyword, roleName));
    }

    /**
     * Retrieves a specific employee by their UUID.
     * Requires permission check on the specific employee record.
     *
     * @param id the UUID of the employee
     * @return the Employee entity
     */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * Updates an existing employee's details.
     * Requires permission check on the specific employee record.
     *
     * @param id      the UUID of the employee
     * @param request the updated employee details
     * @return the updated Employee entity
     */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    /**
     * Changes the employment status of a specific employee.
     * Requires permission check on the specific employee record.
     *
     * @param id     the UUID of the employee
     * @param status the new employment status
     * @return the updated Employee entity
     */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Employee> changeEmploymentStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(employeeService.changeEmploymentStatus(id, status));
    }

    /**
     * Deletes an employee record.
     * Requires permission check on the specific employee record.
     *
     * @param id the UUID of the employee
     * @return a 204 No Content response
     */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────
    // Address
    // ─────────────────────────────────────────────────────────

    /** Save (replace) present and permanent address for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/address")
    public ResponseEntity<?> saveAddress(@PathVariable UUID id,
                                         @RequestBody EmployeeAddressRequest request) {
        return ResponseEntity.ok(employeeDetailService.saveAddress(id, request));
    }

    /** Get address details for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/address")
    public ResponseEntity<?> getAddress(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getAddress(id));
    }

    // ─────────────────────────────────────────────────────────
    // Emergency Contact
    // ─────────────────────────────────────────────────────────

    /** Save (upsert) emergency contact for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/emergency-contact")
    public ResponseEntity<?> saveEmergencyContact(@PathVariable UUID id,
                                                  @RequestBody EmployeeEmergencyContactRequest request) {
        return ResponseEntity.ok(employeeDetailService.saveEmergencyContact(id, request));
    }

    /** Get emergency contact(s) for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/emergency-contact")
    public ResponseEntity<?> getEmergencyContact(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getEmergencyContacts(id));
    }

    // ─────────────────────────────────────────────────────────
    // Statutory Details
    // ─────────────────────────────────────────────────────────

    /** Save (upsert) statutory details for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/statutory")
    public ResponseEntity<?> saveStatutory(@PathVariable UUID id,
                                           @RequestBody EmployeeStatutoryRequest request) {
        return ResponseEntity.ok(employeeDetailService.saveStatutory(id, request));
    }

    /** Get statutory details for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/statutory")
    public ResponseEntity<?> getStatutory(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getStatutory(id));
    }

    // ─────────────────────────────────────────────────────────
    // Bank Account
    // ─────────────────────────────────────────────────────────

    /** Save (upsert primary) bank account for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/bank-account")
    public ResponseEntity<?> saveBankAccount(@PathVariable UUID id,
                                             @RequestBody EmployeeBankAccountRequest request) {
        return ResponseEntity.ok(employeeDetailService.saveBankAccount(id, request));
    }

    /** Get bank account(s) for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/bank-account")
    public ResponseEntity<?> getBankAccounts(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getBankAccounts(id));
    }

    // ─────────────────────────────────────────────────────────
    // Documents
    // ─────────────────────────────────────────────────────────

    /** Replace the entire documents list for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/documents")
    public ResponseEntity<?> saveDocuments(@PathVariable UUID id,
                                           @RequestBody List<EmployeeDocumentRequest> requests) {
        return ResponseEntity.ok(employeeDetailService.saveDocuments(id, requests));
    }

    /** Get all documents for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/documents")
    public ResponseEntity<?> getDocuments(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getDocuments(id));
    }

    // ─────────────────────────────────────────────────────────
    // Salary Revision
    // ─────────────────────────────────────────────────────────

    /** Append a new salary revision record (historical, not replaced). */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PostMapping("/{id}/salary-revision")
    public ResponseEntity<?> addSalaryRevision(@PathVariable UUID id,
                                               @RequestBody EmployeeSalaryRevisionRequest request) {
        return ResponseEntity.ok(employeeDetailService.addSalaryRevision(id, request));
    }

    /** Get all salary revision records for an employee (newest first). */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/salary-revision")
    public ResponseEntity<?> getSalaryRevisions(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getSalaryRevisions(id));
    }

    // ─────────────────────────────────────────────────────────
    // Education
    // ─────────────────────────────────────────────────────────

    /** Replace the entire education list for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/educations")
    public ResponseEntity<?> saveEducations(@PathVariable UUID id,
                                            @RequestBody List<EmployeeEducationRequest> requests) {
        return ResponseEntity.ok(employeeDetailService.saveEducations(id, requests));
    }

    /** Get education history for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/educations")
    public ResponseEntity<?> getEducations(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getEducations(id));
    }

    // ─────────────────────────────────────────────────────────
    // Family / Nominee
    // ─────────────────────────────────────────────────────────

    /** Replace the entire family/nominee list for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/families")
    public ResponseEntity<?> saveFamilies(@PathVariable UUID id,
                                          @RequestBody List<EmployeeFamilyRequest> requests) {
        return ResponseEntity.ok(employeeDetailService.saveFamilies(id, requests));
    }

    /** Get family/nominee list for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/families")
    public ResponseEntity<?> getFamilies(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getFamilies(id));
    }

    // ─────────────────────────────────────────────────────────
    // Contract
    // ─────────────────────────────────────────────────────────

    /** Save (upsert) employment contract for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}/contract")
    public ResponseEntity<?> saveContract(@PathVariable UUID id,
                                          @RequestBody EmployeeContractRequest request) {
        return ResponseEntity.ok(employeeDetailService.saveContract(id, request));
    }

    /** Get employment contract for an employee. */
    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}/contract")
    public ResponseEntity<?> getContract(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeDetailService.getContract(id));
    }
}
