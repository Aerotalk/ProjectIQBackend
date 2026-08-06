package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PermissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, UUID> {
    List<PermissionRequest> findByOrganizationId(UUID organizationId);
    List<PermissionRequest> findByEmployeeId(UUID employeeId);
}
