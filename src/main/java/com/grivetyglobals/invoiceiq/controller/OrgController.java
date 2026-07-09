package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.grivetyglobals.invoiceiq.security.SecurityUtils;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrgController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping("/profile")
    public ResponseEntity<Organization> getMyOrganization() {
        User user = SecurityUtils.getCurrentUser();
        if (user.getOrganization() == null) {
            return ResponseEntity.ok(null);
        }

        Organization org = organizationRepository.findById(user.getOrganization().getId())
                .orElse(null);
                
        if (org == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(org);
    }

    @PreAuthorize("hasAuthority('org.edit')")
    @PutMapping("/profile")
    public ResponseEntity<Organization> updateMyOrganization(
            @RequestBody com.grivetyglobals.invoiceiq.dto.OrganizationUpdateRequest request) {

        User user = SecurityUtils.getCurrentUser();
        if (user.getOrganization() == null) {
            return ResponseEntity.notFound().build();
        }

        Organization org = organizationRepository.findById(user.getOrganization().getId())
                .orElse(null);
        if (org == null) {
            return ResponseEntity.notFound().build();
        }

        org.setOrganizationName(request.getOrganizationName());
        org.setOrganizationType(request.getOrganizationType());
        org.setIndustry(request.getIndustry());
        org.setLegalName(request.getLegalName());
        org.setOrganizationEmail(request.getOrganizationEmail());
        org.setStatus(request.getStatus() != null ? request.getStatus() : org.getStatus());

        return ResponseEntity.ok(organizationRepository.save(org));
    }
}
