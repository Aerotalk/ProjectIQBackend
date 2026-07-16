package com.grivetyglobals.invoiceiq.repository.finance;

import com.grivetyglobals.invoiceiq.entity.finance.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
