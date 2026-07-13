package com.grivetyglobals.invoiceiq.security;

import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.enums.DataScope;
import com.grivetyglobals.invoiceiq.repository.*;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Custom PermissionEvaluator that handles Data Scope checks.
 * 
 * Used via: @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
 * 
 * Resolution:
 *   1. Check if user has the base permission (via authorities)
 *   2. Look up the user's DataScope for that permission
 *   3. Fetch the target entity and compare ownership based on scope
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionService permissionService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final RoleRepository roleRepository;

    /**
     * Called when the target object is already loaded.
     * e.g. @PreAuthorize("hasPermission(#employee, 'employee.edit')")
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        String requiredPermission = (String) permission;

        // Step 1: Basic authority check
        boolean hasBasePermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredPermission));

        if (!hasBasePermission) {
            log.debug("User lacks base permission: {}", requiredPermission);
            return false;
        }

        // Step 2: Get the user and their data scope for this permission
        if (!(authentication.getPrincipal() instanceof User)) {
            return true; // Non-User principal, skip scope check
        }

        User user = (User) authentication.getPrincipal();
        DataScope scope = permissionService.getDataScopeForPermission(user, requiredPermission);

        if (scope == null) {
            log.debug("Permission {} explicitly revoked for user {}", requiredPermission, user.getEmail());
            return false;
        }

        // Step 3: Check scope against the target object
        return checkScopeAgainstObject(user, scope, targetDomainObject, requiredPermission);
    }

    /**
     * Called when only the target ID and type are known (the common case).
     * e.g. @PreAuthorize("hasPermission(#id, 'Employee', 'employee.edit')")
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        String requiredPermission = (String) permission;

        // Step 1: Basic authority check
        boolean hasBasePermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredPermission));

        if (!hasBasePermission) {
            log.debug("User lacks base permission: {}", requiredPermission);
            return false;
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return true;
        }

        User user = (User) authentication.getPrincipal();
        DataScope scope = permissionService.getDataScopeForPermission(user, requiredPermission);

        if (scope == null) {
            log.debug("Permission {} explicitly revoked for user {}", requiredPermission, user.getEmail());
            return false;
        }

        // GLOBAL scope → always allowed, no need to fetch the entity
        if (scope == DataScope.GLOBAL) {
            return true;
        }

        // Step 2: Look up the target entity and check scope
        UUID id = UUID.fromString(targetId.toString());
        return checkScopeAgainstEntity(user, scope, targetType, id, requiredPermission);
    }

    // ========================================================================
    // SCOPE CHECK LOGIC
    // ========================================================================

    /**
     * Checks scope when the domain object is already available.
     */
    private boolean checkScopeAgainstObject(User user, DataScope scope, Object target, String requiredPermission) {
        return switch (scope) {
            case GLOBAL -> true;
            case ORGANIZATION -> checkOrganizationScope(user, target);
            case COMPANY -> checkCompanyScope(user, target, requiredPermission);
            case DEPARTMENT -> checkDepartmentScope(user, target, requiredPermission);
            case TEAM -> checkDepartmentScope(user, target, requiredPermission); // TEAM uses same dept check for now
            case OWN -> checkOwnScope(user, target, requiredPermission);
            case CUSTOM -> true; // CUSTOM scope requires a rules engine — future implementation
        };
    }

    /**
     * Checks scope when only the entity type and ID are known (must fetch from DB).
     */
    private boolean checkScopeAgainstEntity(User user, DataScope scope, String targetType, UUID targetId, String requiredPermission) {
        Object target = fetchEntity(targetType, targetId);
        if (target == null) {
            log.warn("Entity not found: type={}, id={}", targetType, targetId);
            return false;
        }
        return checkScopeAgainstObject(user, scope, target, requiredPermission);
    }

    /**
     * Fetches an entity from the database by type name and ID.
     */
    private Object fetchEntity(String targetType, UUID id) {
        return switch (targetType) {
            case "Employee" -> employeeRepository.findById(id).orElse(null);
            case "Department" -> departmentRepository.findById(id).orElse(null);
            case "Designation" -> designationRepository.findById(id).orElse(null);
            case "Role" -> roleRepository.findById(id).orElse(null);
            default -> {
                log.warn("Unknown target type for permission evaluation: {}", targetType);
                yield null;
            }
        };
    }

    // ========================================================================
    // INDIVIDUAL SCOPE CHECKS
    // ========================================================================

    /**
     * ORGANIZATION scope: user.orgId == target.orgId
     */
    private boolean checkOrganizationScope(User user, Object target) {
        UUID userOrgId = user.getOrganization() != null ? user.getOrganization().getId() : null;
        if (userOrgId == null) return false;

        UUID targetOrgId = extractOrganizationId(target);
        // If target has no org_id (unscoped entity), allow access since user already has the base permission
        if (targetOrgId == null) return true;
        return userOrgId.equals(targetOrgId);
    }

    /**
     * COMPANY scope: userRole.companyId == target.companyId
     */
    private boolean checkCompanyScope(User user, Object target, String requiredPermission) {
        UUID targetCompanyId = extractCompanyId(target);
        
        // If the target entity has no company_id (e.g. an org-level role), 
        // fall back to organization scope check
        if (targetCompanyId == null) {
            return checkOrganizationScope(user, target);
        }
        
        java.util.Set<UUID> allowedCompanyIds = permissionService.getAllowedCompanyIdsForPermission(user, requiredPermission);
        
        if (allowedCompanyIds.contains(null)) {
            // Org-wide role granted this permission, fallback to org scope check if needed
            return checkOrganizationScope(user, target);
        }
        
        return allowedCompanyIds.contains(targetCompanyId);
    }

    /**
     * DEPARTMENT scope: user's department == target's department
     */
    private boolean checkDepartmentScope(User user, Object target, String requiredPermission) {
        // For department scope, we need to check if the target belongs to the same department
        // This requires the user to have a department association (via Employee)
        if (target instanceof Employee employee) {
            if (employee.getDepartment() == null) return false;
            // We'd need to look up the current user's employee record to get their department
            // For now, fall back to company scope as a reasonable approximation
            return checkCompanyScope(user, target, requiredPermission);
        }
        if (target instanceof Department department) {
            // A user can manage a department if it's in their company
            return checkCompanyScope(user, department, requiredPermission);
        }
        return checkCompanyScope(user, target, requiredPermission);
    }

    /**
     * OWN scope: the target was created by this user
     */
    private boolean checkOwnScope(User user, Object target, String requiredPermission) {
        // For Employee: check if the employee IS the current user
        if (target instanceof Employee employee) {
            return employee.getUser() != null && employee.getUser().getId().equals(user.getId());
        }
        // For other entities: fall back to company scope
        // (OWN scope on departments/roles doesn't make practical sense)
        return checkCompanyScope(user, target, requiredPermission);
    }

    // ========================================================================
    // HELPER: Extract IDs from entities
    // ========================================================================

    private UUID extractOrganizationId(Object target) {
        if (target instanceof Employee e) return e.getOrganization() != null ? e.getOrganization().getId() : null;
        if (target instanceof Department d) return d.getOrganization() != null ? d.getOrganization().getId() : null;
        if (target instanceof Designation ds) return ds.getOrganization() != null ? ds.getOrganization().getId() : null;
        if (target instanceof Role r) return r.getOrganization() != null ? r.getOrganization().getId() : null;
        return null;
    }

    private UUID extractCompanyId(Object target) {
        if (target instanceof Employee e) return e.getCompany() != null ? e.getCompany().getId() : null;
        if (target instanceof Department d) return d.getCompany() != null ? d.getCompany().getId() : null;
        if (target instanceof Role r) return r.getCompany() != null ? r.getCompany().getId() : null;
        // Designation doesn't have company_id — fall back to org check
        return null;
    }
}
