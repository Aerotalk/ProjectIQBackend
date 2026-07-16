package com.grivetyglobals.invoiceiq.repository.ticket;

import com.grivetyglobals.invoiceiq.entity.ticket.KnowledgeBaseArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KBRepository extends JpaRepository<KnowledgeBaseArticle, UUID> {
    List<KnowledgeBaseArticle> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
