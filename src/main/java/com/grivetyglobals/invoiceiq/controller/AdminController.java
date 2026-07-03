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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/organizations")
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationCreateRequest request) {
        return ResponseEntity.ok(adminService.createOrganization(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        return ResponseEntity.ok(adminService.createCompany(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(adminService.getAllCompanies(organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable UUID id, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(adminService.getCompanyById(id, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyUpdateRequest request, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(adminService.updateCompany(id, request, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PatchMapping("/companies/{id}/status")
    public ResponseEntity<Company> updateCompanyStatus(@PathVariable UUID id, @RequestParam String status, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(adminService.updateCompanyStatus(id, status, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id, @RequestParam UUID organizationId) {
        adminService.deleteCompany(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}
