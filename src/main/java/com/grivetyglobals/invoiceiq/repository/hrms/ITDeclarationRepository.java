package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ITDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITDeclarationRepository extends JpaRepository<ITDeclaration, UUID> {
    List<ITDeclaration> findByOrganizationId(UUID organizationId);
    List<ITDeclaration> findByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
    Optional<ITDeclaration> findByOrganizationIdAndEmployeeIdAndFinancialYear(UUID organizationId, UUID employeeId, String financialYear);
}
