package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, UUID> {
    Optional<EmployeeContract> findByEmployeeId(UUID employeeId);
}
