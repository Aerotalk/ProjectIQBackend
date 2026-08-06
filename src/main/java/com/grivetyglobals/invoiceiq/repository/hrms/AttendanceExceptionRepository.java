package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceExceptionRepository extends JpaRepository<AttendanceException, UUID> {
    List<AttendanceException> findByOrganizationId(UUID organizationId);
}
