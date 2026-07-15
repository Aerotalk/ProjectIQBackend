package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.RoleRequest;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
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
    private final com.grivetyglobals.invoiceiq.service.AdminService adminService;

    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PreAuthorize("hasPermission(#id, 'Role', 'role.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PreAuthorize("hasPermission(#id, 'Role', 'role.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @PreAuthorize("hasPermission(#id, 'Role', 'role.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasPermission(#id, 'Role', 'role.create')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<Role> cloneRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.cloneRole(id));
    }

    @PreAuthorize("hasPermission(#id, 'Role', 'role.assign')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID id, @RequestParam UUID targetUserId) {
        roleService.assignRoleToUser(id, targetUserId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('role.assign')")
    @PutMapping("/employees/{employeeId}/assign")
    public ResponseEntity<Void> assignRolesToEmployee(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) UUID companyId,
            @RequestBody List<UUID> roleIds) {
        roleService.assignRolesToEmployee(employeeId, companyId, roleIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employees/{employeeId}/roles")
    public ResponseEntity<List<Role>> getAssignedRolesForEmployee(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(roleService.getAssignedRolesForEmployee(employeeId, companyId));
    }

    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping("/custom")
    public ResponseEntity<Role> createCustomRole(@RequestBody com.grivetyglobals.invoiceiq.dto.RoleCreateRequest request) {
        return ResponseEntity.ok(adminService.createRole(request));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Role>> getAvailableRoles() {
        return ResponseEntity.ok(adminService.getAvailableRoles());
    }

    @PostMapping("/{roleId}/permissions/assign")
    public ResponseEntity<Role> assignPermissionsToRole(
            @PathVariable UUID roleId, 
            @RequestBody com.grivetyglobals.invoiceiq.dto.RolePermissionAssignRequest request) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(roleId, request));
    }
}
