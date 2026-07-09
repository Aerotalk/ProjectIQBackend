package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.PermissionGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionGroupMappingRepository extends JpaRepository<PermissionGroupMapping, UUID> {
}
