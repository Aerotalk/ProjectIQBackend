package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ITDeclarationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITDeclarationItemRepository extends JpaRepository<ITDeclarationItem, UUID> {
    List<ITDeclarationItem> findByDeclarationId(UUID declarationId);
    void deleteByDeclarationId(UUID declarationId);
}
