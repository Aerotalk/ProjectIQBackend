package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, UUID> {
    List<Designation> findByOrganizationId(UUID organizationId);

    @Query("SELECT d FROM Designation d WHERE d.organization.id = :organizationId AND (:companyId IS NULL OR d.company.id = :companyId)")
    List<Designation> findByOrganizationIdAndCompanyId(@Param("organizationId") UUID organizationId, @Param("companyId") UUID companyId);
}
