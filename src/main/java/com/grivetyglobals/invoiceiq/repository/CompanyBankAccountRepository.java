package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.CompanyBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for CompanyBankAccountRepository.
 */
@Repository
public interface CompanyBankAccountRepository extends JpaRepository<CompanyBankAccount, UUID> {
}
