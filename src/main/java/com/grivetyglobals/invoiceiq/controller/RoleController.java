package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.RoleRequest;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request, @RequestParam UUID userId, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(roleService.createRole(request, userId, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request, @RequestParam UUID userId, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(roleService.updateRole(id, request, userId, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam UUID organizationId) {
        roleService.deleteRole(id, userId, organizationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<Role> cloneRole(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam UUID organizationId) {
        return ResponseEntity.ok(roleService.cloneRole(id, userId, organizationId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID id, @RequestParam UUID targetUserId, @RequestParam UUID userId, @RequestParam UUID organizationId) {
        roleService.assignRoleToUser(id, targetUserId, userId, organizationId);
        return ResponseEntity.ok().build();
    }
}
