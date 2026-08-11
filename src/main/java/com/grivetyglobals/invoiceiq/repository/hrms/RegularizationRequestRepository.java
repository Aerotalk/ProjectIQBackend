package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.RegularizationRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegularizationRequestRepository extends JpaRepository<RegularizationRequest, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<RegularizationRequest> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<RegularizationRequest> findByOrganizationIdAndStatus(UUID organizationId, String status);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<RegularizationRequest> findByEmployeeId(UUID employeeId);
}

