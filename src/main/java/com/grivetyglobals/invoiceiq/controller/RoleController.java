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
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
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
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<Role> cloneRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.cloneRole(id));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ORGANIZATION_ADMIN')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID id, @RequestParam UUID userId) {
        roleService.assignRoleToUser(id, userId);
        return ResponseEntity.ok().build();
    }
}
