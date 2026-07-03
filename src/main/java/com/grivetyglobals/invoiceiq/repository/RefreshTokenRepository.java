package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByCompany(Company company);
}
