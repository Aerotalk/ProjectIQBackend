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

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @PostMapping
    public ResponseEntity<Designation> createDesignation(@Valid @RequestBody DesignationRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(designationService.createDesignation(request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Designation>> getAllDesignations(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(designationService.getAllDesignations(organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Designation> getDesignationById(@PathVariable UUID id, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(designationService.getDesignationById(id, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Designation> updateDesignation(@PathVariable UUID id, @Valid @RequestBody DesignationRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(designationService.updateDesignation(id, request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_COMPANY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable UUID id, @RequestParam UUID organizationId) {
        designationService.deleteDesignation(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}
