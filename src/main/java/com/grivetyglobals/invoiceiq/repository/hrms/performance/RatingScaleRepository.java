package com.grivetyglobals.invoiceiq.repository.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.RatingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingScaleRepository extends JpaRepository<RatingScale, UUID> {
    List<RatingScale> findByOrganizationId(UUID organizationId);
}
