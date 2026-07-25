package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.ClientDto;
import com.grivetyglobals.invoiceiq.service.sales.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing clients in the sales module.
 * Provides endpoints for creating, reading, updating, and deleting clients.
 */
@RestController
@RequestMapping("/api/admin/sales/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * Retrieves all clients for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of ClientDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ClientDto>> getClients(@RequestParam UUID companyId) {
        return ResponseEntity.ok(clientService.getClientsByCompany(companyId));
    }

    /**
     * Retrieves a specific client by their UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the client
     * @return the ClientDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    /**
     * Creates a new client for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the client data payload
     * @return the created ClientDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestParam UUID companyId, @RequestBody ClientDto dto) {
        return ResponseEntity.ok(clientService.createClient(companyId, dto));
    }

    /**
     * Updates an existing client.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the client to update
     * @param dto the updated client data payload
     * @return the updated ClientDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id, @RequestBody ClientDto dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    /**
     * Deletes a client by their UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the client to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
