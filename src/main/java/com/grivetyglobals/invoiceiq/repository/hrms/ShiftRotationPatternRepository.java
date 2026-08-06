package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ShiftRotationPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRotationPatternRepository extends JpaRepository<ShiftRotationPattern, UUID> {
    List<ShiftRotationPattern> findByOrganizationId(UUID organizationId);
}
