package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.AppraisalCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppraisalCycleRepository extends JpaRepository<AppraisalCycle, UUID> {
    List<AppraisalCycle> findByOrganizationId(UUID organizationId);
}
