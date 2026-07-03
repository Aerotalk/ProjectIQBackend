package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.Application;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @GetMapping("/matrix")
    public ResponseEntity<List<Application>> getPermissionMatrix() {
        return ResponseEntity.ok(permissionService.getPermissionMatrix());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<Role> updateRolePermissions(@PathVariable UUID roleId, @RequestBody Set<UUID> permissionIds) {
        return ResponseEntity.ok(permissionService.updateRolePermissions(roleId, permissionIds));
    }
}
