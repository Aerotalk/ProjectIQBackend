package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeePositionChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for EmployeePositionChange entity.
 */
@Repository
public interface EmployeePositionChangeRepository extends JpaRepository<EmployeePositionChange, UUID> {

    List<EmployeePositionChange> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    void deleteByEmployeeId(UUID employeeId);
}
