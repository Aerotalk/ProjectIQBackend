package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeStatutory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeStatutoryRepository extends JpaRepository<EmployeeStatutory, UUID> {
    Optional<EmployeeStatutory> findByEmployeeId(UUID employeeId);
}
