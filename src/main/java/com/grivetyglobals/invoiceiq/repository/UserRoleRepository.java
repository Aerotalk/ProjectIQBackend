package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for UserRoleRepository.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}
