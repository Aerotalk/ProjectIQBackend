package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for PermissionRepository.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    java.util.Optional<Permission> findByPermissionKey(String permissionKey);

    @org.springframework.data.jpa.repository.Query("SELECT p.permissionKey FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.rolePermissions rp " +
           "JOIN rp.permission p " +
           "WHERE ur.user.id = :userId")
    java.util.Set<String> findDirectRolePermissionsByUserId(@org.springframework.data.repository.query.Param("userId") UUID userId);

    @org.springframework.data.jpa.repository.Query("SELECT p.permissionKey FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.rolePermissionGroups rpg " +
           "JOIN rpg.permissionGroup pg " +
           "JOIN pg.permissions pgm " +
           "JOIN pgm.permission p " +
           "WHERE ur.user.id = :userId")
    java.util.Set<String> findGroupRolePermissionsByUserId(@org.springframework.data.repository.query.Param("userId") UUID userId);

    @org.springframework.data.jpa.repository.Query("SELECT rpg.dataScope FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.rolePermissionGroups rpg " +
           "JOIN rpg.permissionGroup pg " +
           "JOIN pg.permissions pgm " +
           "JOIN pgm.permission p " +
           "WHERE ur.user.id = :userId AND p.permissionKey = :permissionKey")
    java.util.List<com.grivetyglobals.invoiceiq.enums.DataScope> findDataScopesForPermission(
           @org.springframework.data.repository.query.Param("userId") UUID userId,
           @org.springframework.data.repository.query.Param("permissionKey") String permissionKey);

    @org.springframework.data.jpa.repository.Query("SELECT c.id FROM UserRole ur " +
           "LEFT JOIN ur.company c " +
           "JOIN ur.role r " +
           "JOIN r.rolePermissionGroups rpg " +
           "JOIN rpg.permissionGroup pg " +
           "JOIN pg.permissions pgm " +
           "JOIN pgm.permission p " +
           "WHERE ur.user.id = :userId AND p.permissionKey = :permissionKey")
    java.util.List<UUID> findAllowedCompanyIdsForPermission(
           @org.springframework.data.repository.query.Param("userId") UUID userId,
           @org.springframework.data.repository.query.Param("permissionKey") String permissionKey);
}
