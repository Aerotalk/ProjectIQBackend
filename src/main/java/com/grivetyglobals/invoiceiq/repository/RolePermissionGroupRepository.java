package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.RolePermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionGroupRepository extends JpaRepository<RolePermissionGroup, UUID> {
    Optional<RolePermissionGroup> findByRoleIdAndPermissionGroupId(UUID roleId, UUID permissionGroupId);
    List<RolePermissionGroup> findByRoleId(UUID roleId);
}
