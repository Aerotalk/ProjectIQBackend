package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {
    
    Optional<DocumentTemplate> findByFilename(String filename);
    
    List<DocumentTemplate> findByTypeContainingIgnoreCase(String type);
    
}
