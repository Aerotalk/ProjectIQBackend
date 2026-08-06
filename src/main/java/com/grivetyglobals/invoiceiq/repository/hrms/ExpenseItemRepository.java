package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, UUID> {
    List<ExpenseItem> findByClaimId(UUID claimId);
    void deleteByClaimId(UUID claimId);
}
