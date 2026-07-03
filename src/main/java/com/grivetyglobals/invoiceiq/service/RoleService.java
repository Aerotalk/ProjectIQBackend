package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.RoleRequest;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public Role createRole(RoleRequest request, UUID userId, UUID organizationId) {
        Role role = Role.builder()
                .name(request.getName())
                .build();
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_CREATED", "Created role " + request.getName(), saved.getId(), "Role", userId, organizationId);
        return saved;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public Role updateRole(UUID id, RoleRequest request, UUID userId, UUID organizationId) {
        Role role = getRoleById(id);
        role.setName(request.getName());
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_UPDATED", "Updated role " + request.getName(), saved.getId(), "Role", userId, organizationId);
        return saved;
    }

    @Transactional
    public void deleteRole(UUID id, UUID userId, UUID organizationId) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
        auditService.logActivity("ROLE_DELETED", "Deleted role " + role.getName(), id, "Role", userId, organizationId);
    }

    @Transactional
    public Role cloneRole(UUID id, UUID userId, UUID organizationId) {
        Role existingRole = getRoleById(id);
        Role clonedRole = Role.builder()
                .name(existingRole.getName() + " - Copy")
                .build();
        Role saved = roleRepository.save(clonedRole);
        auditService.logActivity("ROLE_CLONED", "Cloned role " + existingRole.getName(), saved.getId(), "Role", userId, organizationId);
        return saved;
    }

    @Transactional
    public void assignRoleToUser(UUID roleId, UUID targetUserId, UUID userId, UUID organizationId) {
        Role role = getRoleById(roleId);
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getRoles().add(role);
        userRepository.save(user);
        auditService.logActivity("ROLE_ASSIGNED", "Assigned role " + role.getName() + " to user " + user.getEmail(), targetUserId, "User", userId, organizationId);
    }
}
