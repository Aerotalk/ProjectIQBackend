package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeStatutory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeStatutoryRepository extends JpaRepository<EmployeeStatutory, UUID> {
    Optional<EmployeeStatutory> findByEmployeeId(UUID employeeId);

    @Query("SELECT s FROM EmployeeStatutory s WHERE s.employee.organization.id = :orgId")
    List<EmployeeStatutory> findByOrganizationId(@Param("orgId") UUID orgId);
}
