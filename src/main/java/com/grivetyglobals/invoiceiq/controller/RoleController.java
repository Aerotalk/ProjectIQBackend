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

/**
 * REST controller for managing system roles.
 * Provides endpoints for creating, cloning, and assigning roles.
 */
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final com.grivetyglobals.invoiceiq.service.AdminService adminService;

    /**
     * Creates a new role.
     * Requires 'role.create' authority.
     *
     * @param request the role creation payload
     * @return the created Role entity
     */
    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    /**
     * Retrieves all roles in the system.
     * Requires 'role.view' authority.
     *
     * @return a list of Role entities
     */
    @PreAuthorize("hasAuthority('role.view')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    /**
     * Retrieves a specific role by its UUID.
     * Requires permission check on the specific role.
     *
     * @param id the UUID of the role
     * @return the Role entity
     */
    @PreAuthorize("hasPermission(#id, 'Role', 'role.view')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    /**
     * Updates an existing role's details.
     * Requires permission check on the specific role.
     *
     * @param id      the UUID of the role
     * @param request the updated role details
     * @return the updated Role entity
     */
    @PreAuthorize("hasPermission(#id, 'Role', 'role.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    /**
     * Deletes a role by its UUID.
     * Requires permission check on the specific role.
     *
     * @param id the UUID of the role
     * @return a 204 No Content response
     */
    @PreAuthorize("hasPermission(#id, 'Role', 'role.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clones an existing role into a new one.
     * Requires 'role.create' permission relative to the source role.
     *
     * @param id the UUID of the role to clone
     * @return the newly cloned Role entity
     */
    @PreAuthorize("hasPermission(#id, 'Role', 'role.create')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<Role> cloneRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.cloneRole(id));
    }

    /**
     * Assigns a role to a specific user.
     * Requires permission check on the role assignment capability.
     *
     * @param id           the UUID of the role
     * @param targetUserId the UUID of the user to assign the role to
     * @return a 200 OK response
     */
    @PreAuthorize("hasPermission(#id, 'Role', 'role.assign')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID id, @RequestParam UUID targetUserId) {
        roleService.assignRoleToUser(id, targetUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Assigns multiple roles to an employee, optionally scoped to a specific company.
     * Requires 'role.assign' authority.
     *
     * @param employeeId the UUID of the employee
     * @param companyId  the optional company scope
     * @param roleIds    the list of role UUIDs
     * @return a 200 OK response
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PutMapping("/employees/{employeeId}/assign")
    public ResponseEntity<Void> assignRolesToEmployee(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) UUID companyId,
            @RequestBody List<UUID> roleIds) {
        roleService.assignRolesToEmployee(employeeId, companyId, roleIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves roles assigned to a specific employee, optionally filtered by company.
     *
     * @param employeeId the UUID of the employee
     * @param companyId  the UUID of the company (optional)
     * @return a list of Role entities
     */
    @GetMapping("/employees/{employeeId}/roles")
    public ResponseEntity<List<Role>> getAssignedRolesForEmployee(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(roleService.getAssignedRolesForEmployee(employeeId, companyId));
    }

    /**
     * Creates a custom role using an administrative service.
     * Requires 'role.create' authority.
     *
     * @param request the role creation payload
     * @return the created custom Role entity
     */
    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping("/custom")
    public ResponseEntity<Role> createCustomRole(@RequestBody com.grivetyglobals.invoiceiq.dto.RoleCreateRequest request) {
        return ResponseEntity.ok(adminService.createRole(request));
    }

    /**
     * Retrieves all available roles through the administrative service.
     *
     * @return a list of Role entities
     */
    @GetMapping("/available")
    public ResponseEntity<List<Role>> getAvailableRoles() {
        return ResponseEntity.ok(adminService.getAvailableRoles());
    }

    /**
     * Assigns permissions to a role.
     *
     * @param roleId  the UUID of the role
     * @param request the payload specifying the permissions to assign
     * @return the updated Role entity
     */
    @PostMapping("/{roleId}/permissions/assign")
    public ResponseEntity<Role> assignPermissionsToRole(
            @PathVariable UUID roleId, 
            @RequestBody com.grivetyglobals.invoiceiq.dto.RolePermissionAssignRequest request) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(roleId, request));
    }
}
