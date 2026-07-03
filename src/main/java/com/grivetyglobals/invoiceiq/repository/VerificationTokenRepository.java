package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByCompanyAndTokenType(Company company, VerificationToken.TokenType tokenType);
}
