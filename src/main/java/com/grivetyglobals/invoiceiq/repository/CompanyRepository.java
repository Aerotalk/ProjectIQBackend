package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for CompanyRepository.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    org.springframework.data.domain.Page<Company> findByOrganizationId(UUID organizationId, org.springframework.data.domain.Pageable pageable);
    java.util.Optional<Company> findByCompanyCode(String companyCode);
    java.util.Optional<Company> findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT c FROM Company c WHERE c.organization.id = :organizationId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Company> findAllByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT DISTINCT c FROM Company c " +
           "WHERE c.organization.id = :organizationId " +
           "AND c.deletedAt IS NULL " +
           "AND (" +
           "   c.id = (SELECT u.company.id FROM User u WHERE u.id = :userId) " +
           "   OR EXISTS (SELECT 1 FROM UserRole ur WHERE ur.user.id = :userId AND ur.company.id = c.id) " +
           "   OR EXISTS (SELECT 1 FROM Employee e WHERE e.user.id = :userId AND e.company.id = c.id) " +
           ") " +
           "ORDER BY c.createdAt ASC")
    List<Company> findAssignedCompaniesForUser(@Param("organizationId") UUID organizationId, @Param("userId") UUID userId);
}
