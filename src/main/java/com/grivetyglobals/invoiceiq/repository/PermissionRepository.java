package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
}
