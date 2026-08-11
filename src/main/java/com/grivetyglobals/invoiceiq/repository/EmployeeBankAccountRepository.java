package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeBankAccountRepository extends JpaRepository<EmployeeBankAccount, UUID> {
    List<EmployeeBankAccount> findByEmployeeId(UUID employeeId);
    void deleteByEmployeeId(UUID employeeId);

    @Query("SELECT b FROM EmployeeBankAccount b WHERE b.employee.organization.id = :orgId")
    List<EmployeeBankAccount> findByOrganizationId(@Param("orgId") UUID orgId);
}
