package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
       
       @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "department", "designation"})
       Optional<Employee> findByUserId(UUID userId);

       @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "department", "designation"})
       Optional<Employee> findById(UUID id);

       @Query("SELECT COUNT(e) FROM Employee e WHERE e.organization.id = :organizationId")
       long countByOrganizationId(@Param("organizationId") UUID organizationId);

       @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "department", "designation"})
       @Query("SELECT e FROM Employee e WHERE e.organization.id = :organizationId " +
                     "AND (cast(:companyId as uuid) IS NULL OR e.company.id = :companyId) " +
                     "AND (cast(:departmentId as uuid) IS NULL OR e.department.id = :departmentId) " +
                     "AND (:status IS NULL OR e.employmentStatus = :status) " +
                     "AND (CAST(:keyword AS text) IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) "
                     +
                     "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) " +
                     "OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))")
       List<Employee> searchAndFilterEmployees(
                     @Param("organizationId") UUID organizationId,
                     @Param("companyId") UUID companyId,
                     @Param("departmentId") UUID departmentId,
                     @Param("status") String status,
                     @Param("keyword") String keyword);
}
