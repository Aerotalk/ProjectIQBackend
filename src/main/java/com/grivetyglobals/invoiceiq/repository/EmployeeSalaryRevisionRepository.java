package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeSalaryRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeSalaryRevisionRepository extends JpaRepository<EmployeeSalaryRevision, UUID> {
    List<EmployeeSalaryRevision> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    @Query("SELECT s FROM EmployeeSalaryRevision s WHERE s.employee.organization.id = :orgId")
    List<EmployeeSalaryRevision> findByOrganizationId(@Param("orgId") UUID orgId);
}
