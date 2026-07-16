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

    @PreAuthorize("hasAuthority('department.create')")
    @PostMapping
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @PreAuthorize("hasAuthority('department.view')")
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PreAuthorize("hasPermission(#id, 'Department', 'department.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PreAuthorize("hasPermission(#id, 'Department', 'department.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable UUID id,
            @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    @PreAuthorize("hasPermission(#id, 'Department', 'department.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
