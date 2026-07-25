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

/**
 * REST controller for managing employee designations (job titles).
 * Secured by method-level authorization and permission checks.
 */
@RestController
@RequestMapping("/api/admin/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    /**
     * Creates a new designation.
     * Requires 'designation.create' authority.
     *
     * @param request the designation creation payload
     * @return the created Designation entity
     */
    @PreAuthorize("hasAuthority('designation.create')")
    @PostMapping
    public ResponseEntity<Designation> createDesignation(@Valid @RequestBody DesignationRequest request) {
        return ResponseEntity.ok(designationService.createDesignation(request));
    }

    /**
     * Retrieves all designations, optionally filtered by company ID.
     * Requires 'designation.view' authority.
     *
     * @param companyId the UUID of the company (optional)
     * @return a list of Designation entities
     */
    @PreAuthorize("hasAuthority('designation.view')")
    @GetMapping
    public ResponseEntity<List<Designation>> getAllDesignations(
            @RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(designationService.getAllDesignations(companyId));
    }

    /**
     * Retrieves a specific designation by its UUID.
     * Requires permission check on the specific designation.
     *
     * @param id the UUID of the designation
     * @return the Designation entity
     */
    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Designation> getDesignationById(@PathVariable UUID id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    /**
     * Updates an existing designation.
     * Requires permission check on the specific designation.
     *
     * @param id      the UUID of the designation
     * @param request the updated designation details
     * @return the updated Designation entity
     */
    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Designation> updateDesignation(@PathVariable UUID id, @Valid @RequestBody DesignationRequest request) {
        return ResponseEntity.ok(designationService.updateDesignation(id, request));
    }

    /**
     * Deletes a designation by its UUID.
     * Requires permission check on the specific designation.
     *
     * @param id the UUID of the designation
     * @return a 204 No Content response
     */
    @PreAuthorize("hasPermission(#id, 'Designation', 'designation.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable UUID id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.noContent().build();
    }
}
