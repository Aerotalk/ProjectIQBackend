package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeSalaryRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeSalaryRevisionRepository extends JpaRepository<EmployeeSalaryRevision, UUID> {
    List<EmployeeSalaryRevision> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);
}
