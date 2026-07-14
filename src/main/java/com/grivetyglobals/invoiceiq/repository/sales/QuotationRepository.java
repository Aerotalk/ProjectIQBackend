package com.grivetyglobals.invoiceiq.repository.sales;

import com.grivetyglobals.invoiceiq.entity.sales.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    List<Quotation> findByCompanyId(UUID companyId);
}
