package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PermissionRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<PermissionRequest> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<PermissionRequest> findByOrganizationIdAndStatus(UUID organizationId, String status);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<PermissionRequest> findByEmployeeId(UUID employeeId);
}

