package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayComponentRepository extends JpaRepository<PayComponent, UUID> {
    List<PayComponent> findByOrganizationId(UUID organizationId);
}
