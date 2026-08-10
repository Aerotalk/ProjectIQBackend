package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeSeparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for EmployeeSeparation entity.
 */
@Repository
public interface EmployeeSeparationRepository extends JpaRepository<EmployeeSeparation, UUID> {

    Optional<EmployeeSeparation> findByEmployeeId(UUID employeeId);

    void deleteByEmployeeId(UUID employeeId);
}
