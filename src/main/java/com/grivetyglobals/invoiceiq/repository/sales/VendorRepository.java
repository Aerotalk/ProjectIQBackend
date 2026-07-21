package com.grivetyglobals.invoiceiq.repository.sales;

import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    List<Vendor> findByCompanyId(UUID companyId);
    long countByCompanyIdAndVendorNoStartingWith(UUID companyId, String prefix);

    boolean existsByCompanyIdAndGstinIgnoreCase(UUID companyId, String gstin);
    boolean existsByCompanyIdAndGstinIgnoreCaseAndIdNot(UUID companyId, String gstin, UUID id);
    boolean existsByCompanyIdAndPanNumberIgnoreCase(UUID companyId, String panNumber);
    boolean existsByCompanyIdAndPanNumberIgnoreCaseAndIdNot(UUID companyId, String panNumber, UUID id);
}

