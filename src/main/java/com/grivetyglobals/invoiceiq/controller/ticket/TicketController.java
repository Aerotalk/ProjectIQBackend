package com.grivetyglobals.invoiceiq.controller.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.TicketDto;
import com.grivetyglobals.invoiceiq.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing support tickets.
 * Provides endpoints for creating, reading, updating, and deleting tickets.
 */
@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Retrieves all tickets for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of TicketDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TicketDto>> getTickets(@RequestParam UUID companyId) {
        return ResponseEntity.ok(ticketService.getTicketsByCompany(companyId));
    }

    /**
     * Retrieves a specific ticket by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the ticket
     * @return the TicketDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    /**
     * Creates a new ticket for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the ticket data payload
     * @return the created TicketDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestParam UUID companyId, @RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.createTicket(companyId, dto));
    }

    /**
     * Updates an existing ticket.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the ticket to update
     * @param dto the updated ticket data payload
     * @return the updated TicketDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable UUID id, @RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.updateTicket(id, dto));
    }

    /**
     * Deletes a ticket by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the ticket to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
