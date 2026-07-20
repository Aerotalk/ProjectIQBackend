package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.enums.DataScope;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * GET /api/admin/permissions/matrix
     * Returns all permissions grouped by module.
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/matrix")
    public ResponseEntity<Map<String, List<Permission>>> getPermissionMatrix() {
        return ResponseEntity.ok(permissionService.getPermissionMatrix());
    }

    /**
     * PUT /api/admin/permissions/roles/{roleId}
     * Updates direct permissions on a role (replaces all).
     */
    @PreAuthorize("hasAuthority('role.edit')")
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<Role> updateRolePermissions(@PathVariable UUID roleId, @RequestBody Set<UUID> permissionIds) {
        return ResponseEntity.ok(permissionService.updateRolePermissions(roleId, permissionIds));
    }

    /**
     * GET /api/admin/permissions/roles/{roleId}
     * Returns the permission IDs directly assigned to a role.
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Set<UUID>> getRolePermissionIds(@PathVariable UUID roleId) {
        return ResponseEntity.ok(permissionService.getRolePermissionIds(roleId));
    }

    /**
     * PUT /api/admin/permissions/roles/{roleId}/groups/{groupId}
     * Assigns a PermissionGroup to a Role with a DataScope.
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PutMapping("/roles/{roleId}/groups/{groupId}")
    public ResponseEntity<RolePermissionGroup> assignGroupToRole(
            @PathVariable UUID roleId,
            @PathVariable UUID groupId,
            @RequestParam DataScope dataScope) {
        return ResponseEntity.ok(permissionService.assignGroupToRole(roleId, groupId, dataScope));
    }

    /**
     * DELETE /api/admin/permissions/roles/{roleId}/groups/{groupId}
     * Removes a PermissionGroup from a Role.
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @DeleteMapping("/roles/{roleId}/groups/{groupId}")
    public ResponseEntity<Void> removeGroupFromRole(@PathVariable UUID roleId, @PathVariable UUID groupId) {
        permissionService.removeGroupFromRole(roleId, groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/admin/permissions/users/{userId}/overrides/{permissionId}
     * Creates or updates a user-level permission override.
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PutMapping("/users/{userId}/overrides/{permissionId}")
    public ResponseEntity<UserPermission> overrideUserPermission(
            @PathVariable UUID userId,
            @PathVariable UUID permissionId,
            @RequestParam boolean isGranted,
            @RequestParam DataScope dataScope) {
        return ResponseEntity.ok(permissionService.overrideUserPermission(userId, permissionId, isGranted, dataScope));
    }

    /**
     * DELETE /api/admin/permissions/users/{userId}/overrides/{permissionId}
     * Removes a user-level permission override.
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @DeleteMapping("/users/{userId}/overrides/{permissionId}")
    public ResponseEntity<Void> removeUserPermissionOverride(@PathVariable UUID userId, @PathVariable UUID permissionId) {
        permissionService.removeUserPermissionOverride(userId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/admin/permissions/users/{userId}/effective
     * Returns the computed effective permissions for a user.
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/users/{userId}/effective")
    public ResponseEntity<Set<String>> getUserEffectivePermissions(@PathVariable UUID userId) {
        // Build a lightweight User object to pass to the service
        User user = User.builder().id(userId).build();
        return ResponseEntity.ok(permissionService.getEffectivePermissions(user));
    }

    /**
     * GET /api/admin/permissions/users/{userId}/overrides
     * Returns all user-level overrides for a user.
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/users/{userId}/overrides")
    public ResponseEntity<List<UserPermission>> getUserOverrides(@PathVariable UUID userId) {
        return ResponseEntity.ok(permissionService.getUserOverrides(userId));
    }
}
