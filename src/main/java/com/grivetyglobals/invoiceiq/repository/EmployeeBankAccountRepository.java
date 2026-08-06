package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.EmployeeBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeBankAccountRepository extends JpaRepository<EmployeeBankAccount, UUID> {
    List<EmployeeBankAccount> findByEmployeeId(UUID employeeId);
    void deleteByEmployeeId(UUID employeeId);
}
