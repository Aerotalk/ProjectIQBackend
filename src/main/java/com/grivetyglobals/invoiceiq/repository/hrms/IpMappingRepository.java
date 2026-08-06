package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.IpMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IpMappingRepository extends JpaRepository<IpMapping, UUID> {
    List<IpMapping> findByOrganizationId(UUID organizationId);
}
