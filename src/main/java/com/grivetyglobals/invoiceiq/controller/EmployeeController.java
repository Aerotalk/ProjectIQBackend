package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.EmployeeCreateRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeUpdateRequest;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.service.EmployeeService;
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
}
