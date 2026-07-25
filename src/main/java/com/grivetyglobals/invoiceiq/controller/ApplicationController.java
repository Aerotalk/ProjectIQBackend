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

/**
 * REST controller for managing application modules.
 * Handles the assignment of applications (modules) to companies and employees.
 */
@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRegistryService applicationRegistryService;

    /**
     * Creates a new application module.
     * Requires 'setting.edit' authority.
     *
     * @param request the application creation payload
     * @return the created Application entity
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PostMapping
    public ResponseEntity<Application> createApplication(
            @Valid @RequestBody com.grivetyglobals.invoiceiq.dto.ApplicationRequest request) {
        return ResponseEntity.ok(applicationRegistryService.createApplication(request));
    }

    /**
     * Retrieves all registered application modules.
     * Requires 'setting.view' authority.
     *
     * @return a list of Application entities
     */
    @PreAuthorize("hasAuthority('setting.view')")
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationRegistryService.getAllApplications());
    }

    /**
     * Assigns specific application modules to an employee.
     * Requires 'setting.edit' authority.
     *
     * @param employeeId     the UUID of the employee
     * @param applicationIds the list of application UUIDs to assign
     * @return a 200 OK response
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Void> assignApplicationsToEmployee(
            @PathVariable UUID employeeId,
            @RequestBody List<UUID> applicationIds) {
        
        applicationRegistryService.assignApplicationsToEmployee(employeeId, applicationIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Assigns specific application modules to a company.
     * Requires 'setting.edit' authority.
     *
     * @param companyId      the UUID of the company
     * @param applicationIds the list of application UUIDs to assign
     * @return a 200 OK response
     */
    @PreAuthorize("hasAuthority('setting.edit')")
    @PutMapping("/companies/{companyId}")
    public ResponseEntity<Void> assignApplicationsToCompany(
            @PathVariable UUID companyId,
            @RequestBody List<UUID> applicationIds) {
        
        applicationRegistryService.assignApplicationsToCompany(companyId, applicationIds);
        return ResponseEntity.ok().build();
    }
}
