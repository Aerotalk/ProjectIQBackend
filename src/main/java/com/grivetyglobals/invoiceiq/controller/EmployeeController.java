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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeCreateRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(employeeService.createEmployee(request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Employee>> searchAndFilterEmployees(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(employeeService.searchAndFilterEmployees(organizationId, departmentId, status, keyword));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequest request,
            @RequestParam UUID organizationId) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Employee> changeEmploymentStatus(
            @PathVariable UUID id,
            @RequestParam String status,
            @RequestParam UUID organizationId) {
        return ResponseEntity.ok(employeeService.changeEmploymentStatus(id, status, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id, @RequestParam UUID organizationId) {
        employeeService.deleteEmployee(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}
