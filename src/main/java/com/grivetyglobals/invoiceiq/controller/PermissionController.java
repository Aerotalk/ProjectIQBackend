package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.enums.DataScope;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for managing system permissions and their assignments.
 * Supports updating role permissions, groups, and user-level overrides.
 */
@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Retrieves all system permissions grouped by their respective modules.
     * Requires 'permission.view' authority.
     *
     * @return a map of module name to a list of permissions
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/matrix")
    public ResponseEntity<Map<String, List<Permission>>> getPermissionMatrix() {
        return ResponseEntity.ok(permissionService.getPermissionMatrix());
    }

    /**
     * Updates direct permissions assigned to a role, replacing existing ones.
     * Requires 'role.edit' authority.
     *
     * @param roleId        the UUID of the role
     * @param permissionIds the set of new permission UUIDs
     * @return the updated Role entity
     */
    @PreAuthorize("hasAuthority('role.edit')")
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<Role> updateRolePermissions(@PathVariable UUID roleId, @RequestBody Set<UUID> permissionIds) {
        return ResponseEntity.ok(permissionService.updateRolePermissions(roleId, permissionIds));
    }

    /**
     * Retrieves the set of permission IDs directly assigned to a specific role.
     * Requires 'permission.view' authority.
     *
     * @param roleId the UUID of the role
     * @return a set of assigned permission UUIDs
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Set<UUID>> getRolePermissionIds(@PathVariable UUID roleId) {
        return ResponseEntity.ok(permissionService.getRolePermissionIds(roleId));
    }

    /**
     * Assigns a predefined permission group to a role with a specific data scope.
     * Requires 'role.assign' authority.
     *
     * @param roleId    the UUID of the role
     * @param groupId   the UUID of the permission group
     * @param dataScope the data scope restriction (e.g., GLOBAL, COMPANY)
     * @return the assigned RolePermissionGroup entity
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
     * Removes a permission group assignment from a role.
     * Requires 'role.assign' authority.
     *
     * @param roleId  the UUID of the role
     * @param groupId the UUID of the permission group to remove
     * @return a 204 No Content response
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @DeleteMapping("/roles/{roleId}/groups/{groupId}")
    public ResponseEntity<Void> removeGroupFromRole(@PathVariable UUID roleId, @PathVariable UUID groupId) {
        permissionService.removeGroupFromRole(roleId, groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates or updates a granular user-level permission override.
     * Requires 'role.assign' authority.
     *
     * @param userId       the UUID of the user
     * @param permissionId the UUID of the permission
     * @param isGranted    true to grant, false to explicitly deny
     * @param dataScope    the data scope for this override
     * @return the created or updated UserPermission entity
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
     * Removes a user-level permission override.
     * Requires 'role.assign' authority.
     *
     * @param userId       the UUID of the user
     * @param permissionId the UUID of the permission override to remove
     * @return a 204 No Content response
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @DeleteMapping("/users/{userId}/overrides/{permissionId}")
    public ResponseEntity<Void> removeUserPermissionOverride(@PathVariable UUID userId, @PathVariable UUID permissionId) {
        permissionService.removeUserPermissionOverride(userId, permissionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Computes and retrieves the effective permissions for a specific user,
     * taking into account roles, groups, and explicit overrides.
     * Requires 'permission.view' authority.
     *
     * @param userId the UUID of the user
     * @return a set of effective permission strings
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/users/{userId}/effective")
    public ResponseEntity<Set<String>> getUserEffectivePermissions(@PathVariable UUID userId) {
        // Build a lightweight User object to pass to the service
        User user = User.builder().id(userId).build();
        return ResponseEntity.ok(permissionService.getEffectivePermissions(user));
    }

    /**
     * Retrieves all explicit user-level permission overrides for a specific user.
     * Requires 'permission.view' authority.
     *
     * @param userId the UUID of the user
     * @return a list of UserPermission entities
     */
    @PreAuthorize("hasAuthority('permission.view')")
    @GetMapping("/users/{userId}/overrides")
    public ResponseEntity<List<UserPermission>> getUserOverrides(@PathVariable UUID userId) {
        return ResponseEntity.ok(permissionService.getUserOverrides(userId));
    }
}
