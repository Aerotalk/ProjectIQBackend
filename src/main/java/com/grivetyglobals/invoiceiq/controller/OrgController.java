package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrgController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * Allows an Org Admin to fetch their own organization's profile.
     */
    @PreAuthorize("hasAuthority('ROLE_ORG_ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<Organization> getMyOrganization(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization org = user.getOrganization();
        if (org == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(org);
    }

    /**
     * Allows an Org Admin to update their own organization's profile.
     */
    @PreAuthorize("hasAuthority('ROLE_ORG_ADMIN')")
    @PutMapping("/profile")
    public ResponseEntity<Organization> updateMyOrganization(
            Principal principal,
            @RequestBody com.grivetyglobals.invoiceiq.dto.OrganizationUpdateRequest request) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization org = user.getOrganization();
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
