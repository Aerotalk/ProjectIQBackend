package com.grivetyglobals.invoiceiq.controller.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.TicketDto;
import com.grivetyglobals.invoiceiq.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TicketDto>> getTickets(@RequestParam UUID companyId) {
        return ResponseEntity.ok(ticketService.getTicketsByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestParam UUID companyId, @RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.createTicket(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable UUID id, @RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.updateTicket(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
