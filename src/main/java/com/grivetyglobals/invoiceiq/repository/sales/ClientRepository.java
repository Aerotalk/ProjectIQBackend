package com.grivetyglobals.invoiceiq.repository.sales;

import com.grivetyglobals.invoiceiq.entity.sales.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByCompanyId(UUID companyId);
}
