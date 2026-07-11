package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.CompanyCreateRequest;
import com.grivetyglobals.invoiceiq.dto.CompanyUpdateRequest;
import com.grivetyglobals.invoiceiq.dto.OrganizationCreateRequest;
import com.grivetyglobals.invoiceiq.dto.UserCreateRequest;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.service.AdminService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasAuthority('org.create')")
    @PostMapping("/organizations")
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationCreateRequest request) {
        return ResponseEntity.ok(adminService.createOrganization(request));
    }

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping("/organizations")
    public ResponseEntity<Page<Organization>> getAllOrganizations(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllOrganizations(pageable));
    }

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping("/organizations/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getOrganizationById(id));
    }

    @PreAuthorize("hasAuthority('org.edit')")
    @PutMapping("/organizations/{id}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable UUID id, @RequestBody com.grivetyglobals.invoiceiq.dto.OrganizationUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateOrganization(id, request));
    }

    @PreAuthorize("hasAuthority('org.create')")
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        return ResponseEntity.ok(adminService.createCompany(request));
    }

    @PreAuthorize("hasAuthority('employee.create')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PreAuthorize("hasAuthority('employee.view')")
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @PreAuthorize("hasAuthority('employee.edit')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody com.grivetyglobals.invoiceiq.dto.UserUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUser(userId, request));
    }

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping("/companies")
    public ResponseEntity<Page<Company>> getAllCompanies(@RequestParam(required = false) UUID organizationId, Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllCompanies(organizationId, pageable));
    }

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable UUID id, @RequestParam(required = false) UUID organizationId) {
        return ResponseEntity.ok(adminService.getCompanyById(id, organizationId));
    }

    @PreAuthorize("hasAnyAuthority('org.view', 'setting.view')")
    @GetMapping("/company/profile")
    public ResponseEntity<Company> getMyCompanyProfile(java.security.Principal principal) {
        return ResponseEntity.ok(adminService.getMyCompanyProfile(principal.getName()));
    }

    @PreAuthorize("hasAnyAuthority('org.edit', 'setting.edit')")
    @PutMapping("/company/profile")
    public ResponseEntity<Company> updateMyCompanyProfile(
            java.security.Principal principal,
            @RequestBody com.grivetyglobals.invoiceiq.dto.CompanyUpdateRequest request) {
        Company company = adminService.getMyCompanyProfile(principal.getName());
        return ResponseEntity.ok(adminService.updateCompany(company.getId(), request, null));
    }

    @PreAuthorize("hasAuthority('org.edit')")
    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyUpdateRequest request, @RequestParam(required = false) UUID organizationId) {
        return ResponseEntity.ok(adminService.updateCompany(id, request, organizationId));
    }

    @PreAuthorize("hasAuthority('org.edit')")
    @PatchMapping("/companies/{id}/status")
    public ResponseEntity<Company> updateCompanyStatus(@PathVariable UUID id, @RequestParam String status, @RequestParam(required = false) UUID organizationId) {
        return ResponseEntity.ok(adminService.updateCompanyStatus(id, status, organizationId));
    }

    @PreAuthorize("hasAuthority('org.delete')")
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id, @RequestParam(required = false) UUID organizationId) {
        adminService.deleteCompany(id, organizationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions/groups")
    public ResponseEntity<java.util.List<com.grivetyglobals.invoiceiq.entity.PermissionGroup>> getMyPermissionGroups() {
        return ResponseEntity.ok(adminService.getMyPermissionGroups());
    }
}
