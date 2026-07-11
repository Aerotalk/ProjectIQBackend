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

@RestController
@RequestMapping("/api/admin/permission-groups")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionService permissionService;

    /**
     * GET /api/admin/permission-groups
     * Lists all permission groups.
     */
    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping
    public ResponseEntity<List<PermissionGroup>> getAllPermissionGroups() {
        return ResponseEntity.ok(permissionGroupRepository.findAll());
    }

    /**
     * GET /api/admin/permission-groups/{id}
     * Gets a single permission group with its permissions.
     */
    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionGroup> getPermissionGroup(@PathVariable UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + id));
        return ResponseEntity.ok(group);
    }

    /**
     * POST /api/admin/permission-groups
     * Creates a new permission group.
     */
    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping
    public ResponseEntity<PermissionGroup> createPermissionGroup(@RequestBody PermissionGroup group) {
        return ResponseEntity.ok(permissionGroupRepository.save(group));
    }

    /**
     * PUT /api/admin/permission-groups/{id}
     * Updates a permission group's name and description.
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
     * DELETE /api/admin/permission-groups/{id}
     * Deletes a permission group and all its mappings.
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
     * POST /api/admin/permission-groups/{groupId}/permissions/{permissionId}
     * Adds a permission to a group.
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PostMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<PermissionGroupMapping> addPermissionToGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.addPermissionToGroup(groupId, permissionId));
    }

    /**
     * DELETE /api/admin/permission-groups/{groupId}/permissions/{permissionId}
     * Removes a permission from a group.
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
