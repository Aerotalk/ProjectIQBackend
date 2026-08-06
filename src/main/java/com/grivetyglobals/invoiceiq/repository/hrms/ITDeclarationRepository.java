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
    Optional<ITDeclaration> findByEmployeeIdAndFinancialYear(UUID employeeId, String financialYear);
}
