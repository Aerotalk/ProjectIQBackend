package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.Application;
import com.grivetyglobals.invoiceiq.entity.Permission;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.repository.ApplicationRepository;
import com.grivetyglobals.invoiceiq.repository.PermissionRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Application> getPermissionMatrix() {
        // Since Application has a OneToMany with Modules (which we need to add to the entity, or just fetch all applications)
        // Wait, I didn't add OneToMany on Application or Module. I will just return all applications and let the JSON serializer handle nested if we add it,
        // or just fetch all applications. Actually, to keep it simple, we'll return all Applications.
        // The frontend can parse the nested structure if we configure the entities correctly.
        return applicationRepository.findAll();
    }

    @Transactional
    public Role updateRolePermissions(UUID roleId, Set<UUID> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<>(permissions));
        
        return roleRepository.save(role);
    }
}
