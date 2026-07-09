package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {
    Optional<PermissionGroup> findByGroupName(String groupName);
}
