package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.FinalSettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinalSettlementItemRepository extends JpaRepository<FinalSettlementItem, UUID> {
    List<FinalSettlementItem> findBySettlementId(UUID settlementId);
    void deleteBySettlementId(UUID settlementId);
}
