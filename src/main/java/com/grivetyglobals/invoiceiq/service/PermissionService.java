package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.Permission;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.repository.PermissionRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grivetyglobals.invoiceiq.entity.RolePermission;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Map<String, List<Permission>> getPermissionMatrix() {
        List<Permission> allPermissions = permissionRepository.findAll();
        return allPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
    }

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
}
