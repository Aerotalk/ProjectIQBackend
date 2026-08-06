package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.FBPDeclarationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FBPDeclarationItemRepository extends JpaRepository<FBPDeclarationItem, UUID> {
    List<FBPDeclarationItem> findByDeclarationId(UUID declarationId);
    void deleteByDeclarationId(UUID declarationId);
}
