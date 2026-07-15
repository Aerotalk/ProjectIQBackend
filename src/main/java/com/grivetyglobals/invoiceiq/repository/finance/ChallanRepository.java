package com.grivetyglobals.invoiceiq.repository.finance;

import com.grivetyglobals.invoiceiq.entity.finance.Challan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallanRepository extends JpaRepository<Challan, UUID> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"linkedVendorPo"})
    List<Challan> findByCompanyId(UUID companyId);
}
