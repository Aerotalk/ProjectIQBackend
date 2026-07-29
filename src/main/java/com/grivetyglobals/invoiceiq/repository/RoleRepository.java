package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for RoleRepository.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(String roleName);

    @Query("SELECT r FROM Role r WHERE r.systemRole = true OR r.organization.id = :orgId OR r.company.id = :companyId")
    List<Role> findAvailableRoles(@Param("orgId") UUID orgId, @Param("companyId") UUID companyId);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
    long countUsersByRoleId(@Param("roleId") UUID roleId);

    /**
     * Fetches all roles scoped to the given organization (or system roles), with their
     * rolePermissions and rolePermissionGroups eagerly joined in a single query to
     * prevent N+1 queries during permission filtering in RoleService.getAllRoles().
     *
     * Note: Uses DISTINCT to avoid duplicate roles from the join.
     */
    @Query("SELECT DISTINCT r FROM Role r "
         + "LEFT JOIN FETCH r.rolePermissions rp "
         + "LEFT JOIN FETCH rp.permission "
         + "LEFT JOIN FETCH r.rolePermissionGroups rpg "
         + "LEFT JOIN FETCH rpg.permissionGroup pg "
         + "LEFT JOIN FETCH pg.permissions pgm "
         + "LEFT JOIN FETCH pgm.permission "
         + "WHERE r.systemRole = true OR r.organization.id = :orgId")
    List<Role> findAllWithPermissionsByOrgId(@Param("orgId") UUID orgId);
}
