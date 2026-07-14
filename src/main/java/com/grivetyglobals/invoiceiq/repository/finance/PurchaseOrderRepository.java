package com.grivetyglobals.invoiceiq.repository.finance;

import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findByCompanyId(UUID companyId);
}
