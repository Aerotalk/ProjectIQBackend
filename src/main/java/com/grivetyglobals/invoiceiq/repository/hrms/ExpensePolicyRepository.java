package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.ExpensePolicy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpensePolicyRepository extends JpaRepository<ExpensePolicy, UUID> {
    @EntityGraph(attributePaths = {"category"})
    List<ExpensePolicy> findByOrganizationId(UUID organizationId);
    
    @EntityGraph(attributePaths = {"category"})
    Optional<ExpensePolicy> findByCategoryIdAndGrade(UUID categoryId, String grade);
}
