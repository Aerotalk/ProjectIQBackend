package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LeaveScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveSchemeRepository extends JpaRepository<LeaveScheme, UUID> {
    List<LeaveScheme> findByOrganizationId(UUID organizationId);
}
