package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.FinalSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinalSettlementRepository extends JpaRepository<FinalSettlement, UUID> {
    List<FinalSettlement> findByOrganizationId(UUID organizationId);
    List<FinalSettlement> findByEmployeeId(UUID employeeId);
}
