package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.AttendanceException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceExceptionRepository extends JpaRepository<AttendanceException, UUID> {
    @EntityGraph(attributePaths = {"employee", "resolvedBy"})
    List<AttendanceException> findByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"employee", "resolvedBy"})
    List<AttendanceException> findByOrganizationIdAndResolved(UUID organizationId, Boolean resolved);
}
