package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.enums.DataScope;
import com.grivetyglobals.invoiceiq.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionGroupMappingRepository permissionGroupMappingRepository;
    private final RolePermissionGroupRepository rolePermissionGroupRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final org.springframework.cache.CacheManager cacheManager;

    // ========================================================================
    // PERMISSION MATRIX
    // ========================================================================

    public Map<String, List<Permission>> getPermissionMatrix() {
        List<Permission> allPermissions = permissionRepository.findAll();
        DataScope scope = getCurrentUserMaxScope();
        
        java.util.Set<String> superAdminOnly = java.util.Set.of("org.create", "org.delete");
        
        if (scope != DataScope.GLOBAL) {
            User currentUser = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser();
            java.util.Set<String> effectiveKeys = getEffectivePermissions(currentUser);
            
            allPermissions = allPermissions.stream()
                    .filter(p -> effectiveKeys.contains(p.getPermissionKey()))
                    .filter(p -> !superAdminOnly.contains(p.getPermissionKey()))
                    .collect(Collectors.toList());
        } else {
            allPermissions = allPermissions.stream()
                    .filter(p -> !superAdminOnly.contains(p.getPermissionKey()))
                    .collect(Collectors.toList());
        }

        return allPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
    }

    // ========================================================================
    // ROLE → DIRECT PERMISSION MANAGEMENT
    // ========================================================================

    @Transactional
    public Role updateRolePermissions(UUID roleId, Set<UUID> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        role.getRolePermissions().clear();
        Set<RolePermission> newPermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .collect(Collectors.toSet());
        role.getRolePermissions().addAll(newPermissions);

        return roleRepository.save(role);
    }

    // ========================================================================
    // ROLE → PERMISSION GROUP MANAGEMENT
    // ========================================================================

    /**
     * Assigns a PermissionGroup to a Role with a specific DataScope.
     * If the group is already assigned to the role, updates the DataScope.
     */
    @Transactional
    public RolePermissionGroup assignGroupToRole(UUID roleId, UUID groupId, DataScope dataScope) {
        DataScope currentUserMaxScope = getCurrentUserMaxScope();
        if (dataScope.ordinal() < currentUserMaxScope.ordinal()) {
            throw new RuntimeException("Security Violation: Cannot grant a DataScope (" + dataScope + ") broader than your own maximum scope (" + currentUserMaxScope + ")");
        }

        if (!currentUserHasGroup(groupId)) {
            throw new RuntimeException("Security Violation: Cannot grant a Permission Group that you do not possess yourself.");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        PermissionGroup group = permissionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + groupId));

        // Check if already assigned — if so, just update the scope
        Optional<RolePermissionGroup> existing = rolePermissionGroupRepository
                .findByRoleIdAndPermissionGroupId(roleId, groupId);

        if (existing.isPresent()) {
            RolePermissionGroup rpg = existing.get();
            rpg.setDataScope(dataScope);
            return rolePermissionGroupRepository.save(rpg);
        }

        // Create new mapping
        RolePermissionGroup rpg = RolePermissionGroup.builder()
                .role(role)
                .permissionGroup(group)
                .dataScope(dataScope)
                .build();
        return rolePermissionGroupRepository.save(rpg);
    }

    /**
     * Removes a PermissionGroup from a Role.
     */
    @Transactional
    public void removeGroupFromRole(UUID roleId, UUID groupId) {
        RolePermissionGroup rpg = rolePermissionGroupRepository
                .findByRoleIdAndPermissionGroupId(roleId, groupId)
                .orElseThrow(() -> new RuntimeException(
                        "Group " + groupId + " is not assigned to Role " + roleId));
        rolePermissionGroupRepository.delete(rpg);
    }

    // ========================================================================
    // USER PERMISSION OVERRIDES
    // ========================================================================

    /**
     * Creates or updates a user-level permission override.
     * isGranted=true → grants a permission the user's roles don't provide
     * isGranted=false → revokes a permission the user's roles do provide
     */
    @Transactional
    public UserPermission overrideUserPermission(UUID userId, UUID permissionId, boolean isGranted, DataScope dataScope) {
        DataScope currentUserMaxScope = getCurrentUserMaxScope();
        if (dataScope != null && dataScope.ordinal() < currentUserMaxScope.ordinal()) {
            throw new RuntimeException("Security Violation: Cannot grant an override DataScope (" + dataScope + ") broader than your own maximum scope (" + currentUserMaxScope + ")");
        }

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        // Check if an override already exists for this user+permission pair
        Optional<UserPermission> existing = userPermissionRepository
                .findByUserIdAndPermissionId(userId, permissionId);

        if (existing.isPresent()) {
            UserPermission up = existing.get();
            up.setGranted(isGranted);
            up.setDataScope(dataScope);
            UserPermission saved = userPermissionRepository.save(up);
            evictUserCache(userId);
            return saved;
        }

        // Create new override — we need the User reference
        UserPermission up = UserPermission.builder()
                .user(User.builder().id(userId).build()) // Lightweight reference
                .permission(permission)
                .isGranted(isGranted)
                .dataScope(dataScope)
                .build();
        UserPermission saved = userPermissionRepository.save(up);
        evictUserCache(userId);
        return saved;
    }

    /**
     * Removes a user-level permission override, restoring role-based behavior.
     */
    @Transactional
    public void removeUserPermissionOverride(UUID userId, UUID permissionId) {
        UserPermission up = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .orElseThrow(() -> new RuntimeException(
                        "No override found for user " + userId + " and permission " + permissionId));
        userPermissionRepository.delete(up);
        evictUserCache(userId);
    }

    private void evictUserCache(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            org.springframework.cache.Cache cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict(user.getEmail());
            }
        });
    }

    // ========================================================================
    // EFFECTIVE PERMISSIONS COMPUTATION
    // ========================================================================

    /**
     * Computes the effective set of permission keys for a user.
     * Resolution order:
     *   1. Collect all permissions from Role → Direct Permissions
     *   2. Collect all permissions from Role → PermissionGroups → Permissions
     *   3. Apply UserPermission overrides (grants add, revokes remove)
     */
    @Transactional(readOnly = true)
    public Set<String> getEffectivePermissions(User user) {
        Set<String> permissions = new HashSet<>();

        // 1. Collect from Role → Direct Permissions
        permissions.addAll(permissionRepository.findDirectRolePermissionsByUserId(user.getId()));

        // 2. Collect from Role → PermissionGroups → Permissions
        permissions.addAll(permissionRepository.findGroupRolePermissionsByUserId(user.getId()));

        // 3. Apply UserPermission overrides
        List<UserPermission> overrides = userPermissionRepository.findByUserId(user.getId());
        for (UserPermission override : overrides) {
            String key = override.getPermission().getPermissionKey();
            if (override.isGranted()) {
                permissions.add(key);       // Grant override → add
            } else {
                permissions.remove(key);    // Revoke override → remove
            }
        }

        return permissions;
    }

    /**
     * Gets all company IDs for which the user has been granted a specific permission.
     * A null in the set indicates the permission is granted org-wide.
     */
    @Transactional(readOnly = true)
    public java.util.Set<UUID> getAllowedCompanyIdsForPermission(User detachedUser, String permissionKey) {
        User user = userRepository.findById(detachedUser.getId()).orElse(detachedUser);
        java.util.Set<UUID> allowedCompanyIds = new HashSet<>();
        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                if (userRole.getRole().getRolePermissionGroups() != null) {
                    for (RolePermissionGroup rpg : userRole.getRole().getRolePermissionGroups()) {
                        if (rpg.getPermissionGroup() != null && rpg.getPermissionGroup().getPermissions() != null) {
                            boolean hasPerm = rpg.getPermissionGroup().getPermissions().stream()
                                    .anyMatch(m -> m.getPermission().getPermissionKey().equals(permissionKey));
                            if (hasPerm) {
                                if (userRole.getCompany() != null) {
                                    allowedCompanyIds.add(userRole.getCompany().getId());
                                } else {
                                    allowedCompanyIds.add(null);
                                }
                            }
                        }
                    }
                }
            }
        }
        return allowedCompanyIds;
    }

    /**
     * Returns the most specific DataScope a user has for a given permission key.
     * Used by CustomPermissionEvaluator to determine "which data?" the user can access.
     *
     * Resolution priority:
     *   1. UserPermission override (highest priority)
     *   2. RolePermissionGroup scope
     *   3. Default: infer from user's highest role tier
     */
    @Transactional(readOnly = true)
    public DataScope getDataScopeForPermission(User detachedUser, String permissionKey) {
        User user = userRepository.findById(detachedUser.getId()).orElse(detachedUser);
        
        // 1. Check UserPermission overrides first (highest priority)
        List<UserPermission> overrides = userPermissionRepository.findByUserId(user.getId());
        for (UserPermission override : overrides) {
            if (override.getPermission().getPermissionKey().equals(permissionKey)) {
                if (!override.isGranted()) {
                    return null; // Permission is explicitly revoked
                }
                if (override.getDataScope() != null) {
                    return override.getDataScope();
                }
            }
        }

        // 2. Check RolePermissionGroup scopes
        if (user.getUserRoles() != null) {
            DataScope broadestScope = null;
            for (UserRole userRole : user.getUserRoles()) {
                if (userRole.getRole().getRolePermissionGroups() != null) {
                    for (RolePermissionGroup rpg : userRole.getRole().getRolePermissionGroups()) {
                        if (rpg.getPermissionGroup() != null && rpg.getPermissionGroup().getPermissions() != null) {
                            boolean groupHasPermission = rpg.getPermissionGroup().getPermissions().stream()
                                    .anyMatch(m -> m.getPermission().getPermissionKey().equals(permissionKey));
                            if (groupHasPermission && rpg.getDataScope() != null) {
                                // Keep the broadest scope (lowest ordinal = broadest)
                                if (broadestScope == null || rpg.getDataScope().ordinal() < broadestScope.ordinal()) {
                                    broadestScope = rpg.getDataScope();
                                }
                            }
                        }
                    }
                }
            }
            if (broadestScope != null) {
                return broadestScope;
            }
        }

        // 3. Default: infer from the user's highest role
        return inferDefaultScope(user);
    }

    /**
     * Infers a default DataScope based on the user's role tier.
     */
    private DataScope inferDefaultScope(User user) {
        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                String roleName = userRole.getRole().getRoleName();
                if (roleName.contains("SUPER_ADMIN")) {
                    return DataScope.GLOBAL;
                }
                if (roleName.contains("ORG_ADMIN")) {
                    return DataScope.ORGANIZATION;
                }
                if (roleName.contains("COMPANY_ADMIN")) {
                    return DataScope.COMPANY;
                }
            }
        }
        // Default for regular users
        return DataScope.OWN;
    }

    private DataScope getCurrentUserMaxScope() {
        User currentUser = SecurityUtils.getCurrentUser();
        DataScope maxScope = DataScope.OWN;
        if (currentUser.getUserRoles() != null) {
            for (UserRole ur : currentUser.getUserRoles()) {
                if (ur.getRole() != null && ur.getRole().getRolePermissionGroups() != null) {
                    for (RolePermissionGroup rpg : ur.getRole().getRolePermissionGroups()) {
                        if (rpg.getDataScope() != null && rpg.getDataScope().ordinal() < maxScope.ordinal()) {
                            maxScope = rpg.getDataScope();
                        }
                    }
                }
            }
        }
        return maxScope;
    }

    private boolean currentUserHasGroup(UUID groupId) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getUserRoles() != null) {
            for (UserRole ur : currentUser.getUserRoles()) {
                if (ur.getRole() != null && ur.getRole().getRolePermissionGroups() != null) {
                    for (RolePermissionGroup rpg : ur.getRole().getRolePermissionGroups()) {
                        if (rpg.getPermissionGroup() != null && rpg.getPermissionGroup().getId().equals(groupId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // ========================================================================
    // PERMISSION GROUP MANAGEMENT
    // ========================================================================

    /**
     * Adds a permission to a group.
     */
    @Transactional
    public PermissionGroupMapping addPermissionToGroup(UUID groupId, UUID permissionId) {
        PermissionGroup group = permissionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + groupId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        // Check if already mapped
        boolean alreadyMapped = group.getPermissions().stream()
                .anyMatch(m -> m.getPermission().getId().equals(permissionId));
        if (alreadyMapped) {
            throw new RuntimeException("Permission '" + permission.getPermissionKey()
                    + "' is already in group '" + group.getGroupName() + "'");
        }

        PermissionGroupMapping mapping = PermissionGroupMapping.builder()
                .permissionGroup(group)
                .permission(permission)
                .build();
        return permissionGroupMappingRepository.save(mapping);
    }

    /**
     * Removes a permission from a group.
     */
    @Transactional
    public void removePermissionFromGroup(UUID groupId, UUID permissionId) {
        PermissionGroup group = permissionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Permission Group not found with id: " + groupId));

        PermissionGroupMapping mapping = group.getPermissions().stream()
                .filter(m -> m.getPermission().getId().equals(permissionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Permission not found in this group"));

        group.getPermissions().remove(mapping);
        permissionGroupMappingRepository.delete(mapping);
    }

    /**
     * Returns all user-level overrides for a given user.
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getUserOverrides(UUID userId) {
        return userPermissionRepository.findByUserId(userId);
    }
}
