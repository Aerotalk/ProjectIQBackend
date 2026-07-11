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

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Role createRole(RoleRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = Role.builder()
                .roleName(request.getName())
                .build();
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_CREATED", "Created role " + request.getName(), saved.getId(), "Role", currentUserId, currentOrgId);
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
    public Role updateRole(UUID id, RoleRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(id);
        role.setRoleName(request.getName());
        Role saved = roleRepository.save(role);
        auditService.logActivity("ROLE_UPDATED", "Updated role " + request.getName(), saved.getId(), "Role", currentUserId, currentOrgId);
        return saved;
    }

    @Transactional
    public void deleteRole(UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(id);
        roleRepository.delete(role);
        auditService.logActivity("ROLE_DELETED", "Deleted role " + role.getRoleName(), id, "Role", currentUserId, currentOrgId);
    }

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

    @Transactional
    public void assignRoleToUser(UUID roleId, UUID targetUserId) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Role role = getRoleById(roleId);
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getUserRoles().add(userRole);
        userRepository.save(user);
        auditService.logActivity("ROLE_ASSIGNED", "Assigned role " + role.getRoleName() + " to user " + user.getEmail(), targetUserId, "User", currentUserId, currentOrgId);
    }

    @Transactional
    public void assignRolesToEmployee(UUID employeeId, List<UUID> roleIds) {
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        com.grivetyglobals.invoiceiq.entity.Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User user = employee.getUser();
        if (user == null) {
            throw new RuntimeException("No user associated with this employee");
        }
        
        user.getUserRoles().clear();
        
        for (UUID roleId : roleIds) {
            Role role = getRoleById(roleId);
            com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            user.getUserRoles().add(userRole);
        }
        
        userRepository.save(user);
        auditService.logActivity("ROLES_ASSIGNED", "Assigned multiple roles to user " + user.getEmail(), user.getId(), "User", currentUserId, currentOrgId);
    }
}
