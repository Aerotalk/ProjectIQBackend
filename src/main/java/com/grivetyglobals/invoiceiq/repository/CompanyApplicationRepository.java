package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.CompanyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyApplicationRepository extends JpaRepository<CompanyApplication, UUID> {
    List<CompanyApplication> findByCompanyId(UUID companyId);
    Optional<CompanyApplication> findByCompanyIdAndApplicationId(UUID companyId, UUID applicationId);
}
