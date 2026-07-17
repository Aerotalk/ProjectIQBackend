package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    org.springframework.data.domain.Page<Company> findByOrganizationId(UUID organizationId, org.springframework.data.domain.Pageable pageable);
    java.util.Optional<Company> findByCompanyCode(String companyCode);
    java.util.Optional<Company> findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT c FROM Company c WHERE c.organization.id = :organizationId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Company> findAllByOrganizationId(@Param("organizationId") UUID organizationId);
}
