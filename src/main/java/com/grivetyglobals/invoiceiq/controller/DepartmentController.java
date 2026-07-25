package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.DepartmentRequest;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing organizational departments.
 * Secured by method-level authorization and permission checks.
 */
@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Creates a new department.
     * Requires 'department.create' authority.
     *
     * @param request the department creation payload
     * @return the created Department entity
     */
    @PreAuthorize("hasAuthority('department.create')")
    @PostMapping
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    /**
     * Retrieves a list of all departments, optionally filtered by company ID.
     * Requires 'department.view' authority.
     *
     * @param companyId the UUID of the company to filter by (optional)
     * @return a list of Department entities
     */
    @PreAuthorize("hasAuthority('department.view')")
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments(
            @RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(departmentService.getAllDepartments(companyId));
    }

    /**
     * Retrieves a specific department by its UUID.
     * Requires permission check on the specific department.
     *
     * @param id the UUID of the department
     * @return the Department entity
     */
    @PreAuthorize("hasPermission(#id, 'Department', 'department.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * Updates an existing department.
     * Requires permission check on the specific department.
     *
     * @param id      the UUID of the department
     * @param request the updated department details
     * @return the updated Department entity
     */
    @PreAuthorize("hasPermission(#id, 'Department', 'department.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable UUID id,
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    /**
     * Deletes a department by its UUID.
     * Requires permission check on the specific department.
     *
     * @param id the UUID of the department
     * @return a 204 No Content response
     */
    @PreAuthorize("hasPermission(#id, 'Department', 'department.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
