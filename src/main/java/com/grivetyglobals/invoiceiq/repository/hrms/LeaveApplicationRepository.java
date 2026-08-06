package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, UUID> {
    List<LeaveApplication> findByOrganizationId(UUID organizationId);
    List<LeaveApplication> findByEmployeeId(UUID employeeId);
    List<LeaveApplication> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
