package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(String roleName);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Role r WHERE r.systemRole = true OR r.organization.id = :orgId OR r.company.id = :companyId")
    java.util.List<Role> findAvailableRoles(@org.springframework.data.repository.query.Param("orgId") UUID orgId, @org.springframework.data.repository.query.Param("companyId") UUID companyId);
}
