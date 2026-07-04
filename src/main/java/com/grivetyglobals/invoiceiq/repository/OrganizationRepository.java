package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByOrganizationCode(String organizationCode);
    boolean existsByOrganizationCode(String organizationCode);
}
