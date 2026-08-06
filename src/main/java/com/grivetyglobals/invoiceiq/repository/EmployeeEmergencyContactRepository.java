package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeEmergencyContactRepository extends JpaRepository<EmployeeEmergencyContact, UUID> {
    List<EmployeeEmergencyContact> findByEmployeeId(UUID employeeId);
    Optional<EmployeeEmergencyContact> findFirstByEmployeeId(UUID employeeId);
    void deleteByEmployeeId(UUID employeeId);
}
