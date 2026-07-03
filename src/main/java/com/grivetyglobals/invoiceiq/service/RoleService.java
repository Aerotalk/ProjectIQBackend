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

    @Transactional
    public Role createRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .build();
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public Role updateRole(UUID id, RoleRequest request) {
        Role role = getRoleById(id);
        role.setName(request.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }

    @Transactional
    public Role cloneRole(UUID id) {
        Role existingRole = getRoleById(id);
        Role clonedRole = Role.builder()
                .name(existingRole.getName() + " - Copy")
                .build();
        return roleRepository.save(clonedRole);
    }

    @Transactional
    public void assignRoleToUser(UUID roleId, UUID userId) {
        Role role = getRoleById(roleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
