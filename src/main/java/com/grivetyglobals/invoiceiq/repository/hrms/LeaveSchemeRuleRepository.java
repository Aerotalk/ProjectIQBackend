package com.grivetyglobals.invoiceiq.repository.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.LeaveSchemeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveSchemeRuleRepository extends JpaRepository<LeaveSchemeRule, UUID> {
    List<LeaveSchemeRule> findBySchemeId(UUID schemeId);
    void deleteBySchemeId(UUID schemeId);
}
