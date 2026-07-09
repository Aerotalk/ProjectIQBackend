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

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('employee.view')")
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(Principal principal) {
        return ResponseEntity.ok(employeeService.getMyProfile(principal.getName()));
    }

    @PreAuthorize("hasAuthority('employee.create')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    @PreAuthorize("hasAuthority('employee.view')")
    @GetMapping
    public ResponseEntity<List<Employee>> searchAndFilterEmployees(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(employeeService.searchAndFilterEmployees(departmentId, status, keyword));
    }

    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Employee> changeEmploymentStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(employeeService.changeEmploymentStatus(id, status));
    }

    @PreAuthorize("hasPermission(#id, 'Employee', 'employee.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
