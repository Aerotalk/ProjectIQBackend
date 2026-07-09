package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query("SELECT COUNT(d) FROM Department d WHERE d.organization.id = :organizationId")
    long countByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT d FROM Department d WHERE d.organization.id = :organizationId AND (:companyId IS NULL OR d.company.id = :companyId)")
    List<Department> findByOrganizationIdAndCompanyId(@Param("organizationId") UUID organizationId, @Param("companyId") UUID companyId);
}
