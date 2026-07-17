package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.DesignationRequest;
import com.grivetyglobals.invoiceiq.entity.Designation;
import com.grivetyglobals.invoiceiq.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    @PreAuthorize("hasAuthority('designation.create')")
    @PostMapping
    public ResponseEntity<Designation> createDesignation(@Valid @RequestBody DesignationRequest request) {
        return ResponseEntity.ok(designationService.createDesignation(request));
    }

    @PreAuthorize("hasAuthority('designation.view')")
    @GetMapping
    public ResponseEntity<List<Designation>> getAllDesignations(
            @RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(designationService.getAllDesignations(companyId));
    }

    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Designation> getDesignationById(@PathVariable UUID id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Designation> updateDesignation(@PathVariable UUID id, @Valid @RequestBody DesignationRequest request) {
        return ResponseEntity.ok(designationService.updateDesignation(id, request));
    }

    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable UUID id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.noContent().build();
    }
}
