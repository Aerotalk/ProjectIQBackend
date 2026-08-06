package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceSchemeRepository extends JpaRepository<AttendanceScheme, UUID> {
    List<AttendanceScheme> findByOrganizationId(UUID organizationId);
}
