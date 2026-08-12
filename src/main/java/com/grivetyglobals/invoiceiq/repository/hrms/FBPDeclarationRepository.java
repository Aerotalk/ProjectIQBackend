package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.FBPDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FBPDeclarationRepository extends JpaRepository<FBPDeclaration, UUID> {
    List<FBPDeclaration> findByOrganizationId(UUID organizationId);
    List<FBPDeclaration> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
    Optional<FBPDeclaration> findByEmployeeIdAndFinancialYear(UUID employeeId, String financialYear);
}
