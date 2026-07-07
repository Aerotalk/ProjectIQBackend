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

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @PostMapping
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(departmentService.createDepartment(request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(departmentService.getAllDepartments(organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable UUID id, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id, @RequestParam UUID organizationId) {
        departmentService.deleteDepartment(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}
