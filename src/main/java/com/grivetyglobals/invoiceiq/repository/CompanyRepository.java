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
    List<Company> findByOrganizationId(UUID organizationId);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") UUID organizationId);
}
