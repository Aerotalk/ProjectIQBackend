package com.grivetyglobals.invoiceiq.repository.sales;

import com.grivetyglobals.invoiceiq.entity.sales.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCompanyId(UUID companyId);
}
