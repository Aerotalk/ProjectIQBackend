package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.PermissionGroup;
import com.grivetyglobals.invoiceiq.entity.PermissionGroupMapping;
import com.grivetyglobals.invoiceiq.repository.PermissionGroupRepository;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing permission groups.
 * Allows logical grouping of permissions to simplify role assignments.
 */
@RestController
@RequestMapping("/api/admin/permission-groups")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionService permissionService;

    /**
     * Retrieves all permission groups in the system.
     * Requires 'role.view' authority.
     *
     * @return a list of PermissionGroup entities
     */
    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping
    public ResponseEntity<List<PermissionGroup>> getAllPermissionGroups() {
        return ResponseEntity.ok(permissionGroupRepository.findAll());
    }

    /**
     * Retrieves a specific permission group along with its nested permissions.
     * Requires 'role.view' authority.
     *
     * @param id the UUID of the permission group
     * @return the PermissionGroup entity
     */
    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionGroup> getPermissionGroup(@PathVariable UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + id));
        return ResponseEntity.ok(group);
    }

    /**
     * Creates a new permission group.
     * Requires 'role.create' authority.
     *
     * @param group the permission group payload
     * @return the created PermissionGroup entity
     */
    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping
    public ResponseEntity<PermissionGroup> createPermissionGroup(@RequestBody PermissionGroup group) {
        return ResponseEntity.ok(permissionGroupRepository.save(group));
    }

    /**
     * Updates an existing permission group's name and description.
     * Requires 'role.edit' authority.
     *
     * @param id      the UUID of the permission group
     * @param request the updated permission group details
     * @return the updated PermissionGroup entity
     */
    @PreAuthorize("hasAuthority('role.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<PermissionGroup> updatePermissionGroup(@PathVariable UUID id, @RequestBody PermissionGroup request) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + id));
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        return ResponseEntity.ok(permissionGroupRepository.save(group));
    }

    /**
     * Deletes a permission group and all its mappings.
     * Requires 'role.delete' authority.
     *
     * @param id the UUID of the permission group to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("hasAuthority('role.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermissionGroup(@PathVariable UUID id) {
        if (!permissionGroupRepository.existsById(id)) {
            throw new RuntimeException("Permission Group not found with id: " + id);
        }
        permissionGroupRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a specific permission to a permission group.
     * Requires 'role.assign' authority.
     *
     * @param groupId      the UUID of the permission group
     * @param permissionId the UUID of the permission to add
     * @return the created PermissionGroupMapping entity
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PostMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<PermissionGroupMapping> addPermissionToGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.addPermissionToGroup(groupId, permissionId));
    }

    /**
     * Removes a specific permission from a permission group.
     * Requires 'role.assign' authority.
     *
     * @param groupId      the UUID of the permission group
     * @param permissionId the UUID of the permission to remove
     * @return a 204 No Content response
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID permissionId) {
        permissionService.removePermissionFromGroup(groupId, permissionId);
        return ResponseEntity.noContent().build();
    }
}
