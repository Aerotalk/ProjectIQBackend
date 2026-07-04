package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Application;
import com.grivetyglobals.invoiceiq.service.ApplicationRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRegistryService applicationRegistryService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping
    public ResponseEntity<Application> createApplication(
            @Valid @RequestBody com.grivetyglobals.invoiceiq.dto.ApplicationRequest request, 
            @RequestParam UUID userId, 
            @RequestParam UUID organizationId) {
        return ResponseEntity.ok(applicationRegistryService.createApplication(request, userId, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationRegistryService.getAllApplications());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Void> assignApplicationsToEmployee(
            @PathVariable UUID employeeId,
            @RequestBody List<UUID> applicationIds,
            @RequestParam UUID userId,
            @RequestParam UUID organizationId) {
        
        applicationRegistryService.assignApplicationsToEmployee(employeeId, applicationIds, userId, organizationId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/companies/{companyId}")
    public ResponseEntity<Void> assignApplicationsToCompany(
            @PathVariable UUID companyId,
            @RequestBody List<UUID> applicationIds,
            @RequestParam UUID userId,
            @RequestParam UUID organizationId) {
        
        applicationRegistryService.assignApplicationsToCompany(companyId, applicationIds, userId, organizationId);
        return ResponseEntity.ok().build();
    }
}
