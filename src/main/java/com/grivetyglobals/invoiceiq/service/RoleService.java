package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.RoleRequest;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing roles.
 * Provides functionality for creating, updating, deleting, cloning, and assigning roles.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EmployeeRepository employeeRepository;
    private final com.grivetyglobals.invoiceiq.repository.CompanyRepository companyRepository;
    private final org.springframework.cache.CacheManager cacheManager;
    private final PermissionService permissionService;

    /**
     * Creates a new role.
     * 
     * @param request the role data payload
     * @return the created Role entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "rolesList", allEntries = true)
    @Transactional
    public Role createRole(RoleRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = Role.builder()
                .roleName(request.getName())
                .build();
        
        // Set organization on the role so scope checks work
        if (currentOrgId != null) {
            role.setOrganization(com.grivetyglobals.invoiceiq.entity.Organization.builder().id(currentOrgId).build());
        }
        
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_CREATED", "Created role " + request.getName(), saved.getId(), "Role", currentUserId, currentOrgId);
        return saved;
    }

    /**
     * Retrieves all roles, filtered by the current user's permissions and scope.
     * Non-super-admins cannot see the super admin role.
     *
     * Uses an optimized JOIN FETCH query to load all role-permission data in a single
     * DB round-trip, preventing N+1 queries. Results are cached in "rolesList".
     * 
     * @return a list of accessible Role entities
     */
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "rolesList", key = "#root.methodName + '_' + T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentUser().getId()")
    public List<Role> getAllRoles() {
        User currentUser = SecurityUtils.getCurrentUser();
        currentUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        boolean isSuperAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_SUPER_ADMIN".equals(ur.getRole().getRoleName()));
        boolean isOrgAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_ORG_ADMIN".equals(ur.getRole().getRoleName()));

        UUID orgId = SecurityUtils.getCurrentOrganizationId();

        // Use a single JOIN FETCH query to avoid N+1 on permission collections
        List<Role> allRoles = (orgId != null)
                ? roleRepository.findAllWithPermissionsByOrgId(orgId)
                : roleRepository.findAll();

        // Super admins / org admins: just filter out ROLE_SUPER_ADMIN if not super admin
        if (isSuperAdmin || isOrgAdmin) {
            return allRoles.stream()
                    .filter(role -> !("ROLE_SUPER_ADMIN".equals(role.getRoleName()) && !isSuperAdmin))
                    .filter(role -> !("ROLE_ORG_ADMIN".equals(role.getRoleName()) && !isSuperAdmin && !isOrgAdmin))
                    .collect(java.util.stream.Collectors.toList());
        }

        // For regular users: only show roles whose permissions are a subset of theirs
        java.util.Set<String> myPermissions = permissionService.getEffectivePermissions(currentUser);
        return allRoles.stream()
                .filter(role -> {
                    if ("ROLE_SUPER_ADMIN".equals(role.getRoleName())) return false;
                    if ("ROLE_ORG_ADMIN".equals(role.getRoleName())) return false;
                    java.util.Set<String> rolePermKeys = getEffectivePermissionKeysForRole(role);
                    return myPermissions.containsAll(rolePermKeys);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Computes the effective permission keys for a given Role by collecting:
     * 1. Direct RolePermission entries
     * 2. Permissions from RolePermissionGroups
     */
    private java.util.Set<String> getEffectivePermissionKeysForRole(Role role) {
        java.util.Set<String> keys = new java.util.HashSet<>();

        // Direct permissions
        if (role.getRolePermissions() != null) {
            for (com.grivetyglobals.invoiceiq.entity.RolePermission rp : role.getRolePermissions()) {
                if (rp.getPermission() != null) {
                    keys.add(rp.getPermission().getPermissionKey());
                }
            }
        }

        // Permissions via permission groups
        if (role.getRolePermissionGroups() != null) {
            for (com.grivetyglobals.invoiceiq.entity.RolePermissionGroup rpg : role.getRolePermissionGroups()) {
                if (rpg.getPermissionGroup() != null && rpg.getPermissionGroup().getPermissions() != null) {
                    for (com.grivetyglobals.invoiceiq.entity.PermissionGroupMapping m : rpg.getPermissionGroup().getPermissions()) {
                        if (m.getPermission() != null) {
                            keys.add(m.getPermission().getPermissionKey());
                        }
                    }
                }
            }
        }

        return keys;
    }

    /**
     * Retrieves a role by its UUID.
     * 
     * @param id the UUID of the role
     * @return the Role entity
     */
    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    /**
     * Updates an existing role's details.
     * 
     * @param id      the UUID of the role
     * @param request the updated role data
     * @return the updated Role entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "rolesList", allEntries = true)
    @Transactional
    public Role updateRole(UUID id, RoleRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(id);
        role.setRoleName(request.getName());
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_UPDATED", "Updated role " + request.getName(), saved.getId(), "Role", currentUserId, currentOrgId);
        return saved;
    }

    /**
     * Deletes a role by its UUID.
     * Prevents deletion if the role is currently assigned to users.
     * 
     * @param id the UUID of the role
     */
    @org.springframework.cache.annotation.CacheEvict(value = "rolesList", allEntries = true)
    @Transactional
    public void deleteRole(UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(id);
        
        if (roleRepository.countUsersByRoleId(id) > 0) {
            throw new RuntimeException("Cannot delete this role because it is assigned to one or more users. Please reassign the users first.");
        }
        
        roleRepository.delete(role);
        auditService.logActivity("ROLE_DELETED", "Deleted role " + role.getRoleName(), id, "Role", currentUserId, currentOrgId);
    }

    /**
     * Clones an existing role.
     * 
     * @param id the UUID of the role to clone
     * @return the newly cloned Role entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "rolesList", allEntries = true)
    @Transactional
    public Role cloneRole(UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role existingRole = getRoleById(id);
        Role clonedRole = Role.builder()
                .roleName(existingRole.getRoleName() + " - Copy")
                .build();
        Role saved = roleRepository.save(clonedRole);
        auditService.logActivity("ROLE_CLONED", "Cloned role " + existingRole.getRoleName(), saved.getId(), "Role", currentUserId, currentOrgId);
        return saved;
    }

    /**
     * Assigns a role to a user. Includes privilege escalation checks.
     * 
     * @param roleId       the UUID of the role to assign
     * @param targetUserId the UUID of the target user
     */
    @Transactional
    public void assignRoleToUser(UUID roleId, UUID targetUserId) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(roleId);
        
        // Privilege escalation check
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("Current user not found"));
        boolean isSuperAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_SUPER_ADMIN".equals(ur.getRole().getRoleName()));
        boolean isOrgAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_ORG_ADMIN".equals(ur.getRole().getRoleName()));
        
        if ("ROLE_SUPER_ADMIN".equals(role.getRoleName()) && !isSuperAdmin) {
            throw new RuntimeException("Security Violation: Only Super Admins can assign the Super Admin role.");
        }
        if ("ROLE_ORG_ADMIN".equals(role.getRoleName()) && !isSuperAdmin && !isOrgAdmin) {
            throw new RuntimeException("Security Violation: Only Super Admins or Org Admins can assign the Org Admin role.");
        }
        
        if (!isSuperAdmin && !isOrgAdmin) {
            java.util.Set<String> myPermissions = permissionService.getEffectivePermissions(currentUser);
            java.util.Set<String> rolePermKeys = getEffectivePermissionKeysForRole(role);
            if (!myPermissions.containsAll(rolePermKeys)) {
                throw new RuntimeException("Security Violation: Cannot assign role '" + role.getRoleName() 
                    + "' because it contains permissions you do not have.");
            }
        }
        
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getUserRoles().add(userRole);
        userRepository.save(user);
        auditService.logActivity("ROLE_ASSIGNED", "Assigned role " + role.getRoleName() + " to user " + user.getEmail(), targetUserId, "User", currentUserId, currentOrgId);
        evictUserCache(user.getEmail());
    }

    /**
     * Assigns multiple roles to an employee under a specific company (if provided).
     * Includes privilege escalation checks.
     * 
     * @param employeeId the UUID of the employee
     * @param companyId  the UUID of the company (null for org-level roles)
     * @param roleIds    the list of role UUIDs to assign
     */
    @Transactional
    public void assignRolesToEmployee(UUID employeeId, UUID companyId, List<UUID> roleIds) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        // Privilege escalation check: ensure the assigner can't grant roles with more permissions than they have
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("Current user not found"));
        boolean isSuperAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_SUPER_ADMIN".equals(ur.getRole().getRoleName()));
        boolean isOrgAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> "ROLE_ORG_ADMIN".equals(ur.getRole().getRoleName()));
        
        if (!isSuperAdmin && !isOrgAdmin) {
            java.util.Set<String> myPermissions = permissionService.getEffectivePermissions(currentUser);
            for (UUID roleId : roleIds) {
                Role role = getRoleById(roleId);
                
                if ("ROLE_SUPER_ADMIN".equals(role.getRoleName())) {
                    throw new RuntimeException("Security Violation: Only Super Admins can assign the Super Admin role.");
                }
                if ("ROLE_ORG_ADMIN".equals(role.getRoleName())) {
                    throw new RuntimeException("Security Violation: Only Super Admins or Org Admins can assign the Org Admin role.");
                }
                
                java.util.Set<String> rolePermKeys = getEffectivePermissionKeysForRole(role);
                if (!myPermissions.containsAll(rolePermKeys)) {
                    throw new RuntimeException("Security Violation: Cannot assign role '" + role.getRoleName() 
                        + "' because it contains permissions you do not have.");
                }
            }
        } else {
            for (UUID roleId : roleIds) {
                Role role = getRoleById(roleId);
                if ("ROLE_SUPER_ADMIN".equals(role.getRoleName()) && !isSuperAdmin) {
                    throw new RuntimeException("Security Violation: Only Super Admins can assign the Super Admin role.");
                }
            }
        }
        
        com.grivetyglobals.invoiceiq.entity.Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User user = employee.getUser();
        if (user == null) {
            throw new RuntimeException("No user associated with this employee");
        }
        
        com.grivetyglobals.invoiceiq.entity.Company company = null;
        if (companyId != null) {
            company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        }
        
        if (companyId != null) {
            final UUID cId = companyId;
            user.getUserRoles().removeIf(ur -> ur.getCompany() != null && ur.getCompany().getId().equals(cId));
        } else {
            user.getUserRoles().removeIf(ur -> ur.getCompany() == null);
        }
        
        for (UUID roleId : roleIds) {
            Role role = getRoleById(roleId);
            com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                    .user(user)
                    .role(role)
                    .company(company)
                    .build();
            user.getUserRoles().add(userRole);
        }
        
        userRepository.save(user);
        auditService.logActivity("ROLES_ASSIGNED", "Assigned multiple roles to user " + user.getEmail(), user.getId(), "User", currentUserId, currentOrgId);
        evictUserCache(user.getEmail());
    }

    private void evictUserCache(String email) {
        org.springframework.cache.Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.evict(email);
        }
    }

    /**
     * Retrieves roles assigned to an employee, optionally scoped to a company.
     * 
     * @param employeeId the UUID of the employee
     * @param companyId  the optional UUID of the company
     * @return a list of assigned Role entities
     */
    @Transactional(readOnly = true)
    public List<Role> getAssignedRolesForEmployee(UUID employeeId, UUID companyId) {
        com.grivetyglobals.invoiceiq.entity.Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User user = employee.getUser();
        if (user == null || user.getUserRoles() == null) {
            return java.util.Collections.emptyList();
        }
        
        if (companyId != null) {
            return user.getUserRoles().stream()
                    .filter(ur -> ur.getCompany() != null && ur.getCompany().getId().equals(companyId))
                    .map(com.grivetyglobals.invoiceiq.entity.UserRole::getRole)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Org-level roles: those assigned without a company
            return user.getUserRoles().stream()
                    .filter(ur -> ur.getCompany() == null)
                    .map(com.grivetyglobals.invoiceiq.entity.UserRole::getRole)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
}
