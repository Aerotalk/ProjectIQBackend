package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    org.springframework.data.domain.Page<User> findByOrganizationId(UUID organizationId, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<User> findByCompanyId(UUID companyId, org.springframework.data.domain.Pageable pageable);
    Optional<User> findFirstByCompanyIdAndUserRoles_Role_RoleName(UUID companyId, String roleName);
}
