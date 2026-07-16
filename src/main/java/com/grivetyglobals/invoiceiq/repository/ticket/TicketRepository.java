package com.grivetyglobals.invoiceiq.repository.ticket;

import com.grivetyglobals.invoiceiq.entity.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
