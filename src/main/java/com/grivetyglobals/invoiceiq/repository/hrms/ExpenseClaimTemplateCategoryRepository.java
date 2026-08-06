package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpenseClaimTemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseClaimTemplateCategoryRepository extends JpaRepository<ExpenseClaimTemplateCategory, UUID> {
    List<ExpenseClaimTemplateCategory> findByTemplateId(UUID templateId);
    void deleteByTemplateId(UUID templateId);
}
