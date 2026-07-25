package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.QuotationDto;
import com.grivetyglobals.invoiceiq.service.sales.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing quotations in the sales module.
 * Provides endpoints for creating, reading, updating, and deleting quotations.
 */
@RestController
@RequestMapping("/api/admin/sales/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    /**
     * Retrieves all quotations for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of QuotationDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<QuotationDto>> getQuotations(@RequestParam UUID companyId) {
        return ResponseEntity.ok(quotationService.getQuotationsByCompany(companyId));
    }

    /**
     * Retrieves a specific quotation by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the quotation
     * @return the QuotationDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<QuotationDto> getQuotation(@PathVariable UUID id) {
        return ResponseEntity.ok(quotationService.getQuotation(id));
    }

    /**
     * Creates a new quotation for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the quotation data payload
     * @return the created QuotationDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<QuotationDto> createQuotation(@RequestParam UUID companyId, @RequestBody QuotationDto dto) {
        return ResponseEntity.ok(quotationService.createQuotation(companyId, dto));
    }

    /**
     * Updates an existing quotation.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the quotation to update
     * @param dto the updated quotation data payload
     * @return the updated QuotationDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<QuotationDto> updateQuotation(@PathVariable UUID id, @RequestBody QuotationDto dto) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, dto));
    }

    /**
     * Updates the status of an existing quotation (e.g., APPROVED, REJECTED).
     * Restricted to managers or admins, or those with 'quotation.approve' authority.
     *
     * @param id         the UUID of the quotation
     * @param status     the new status string
     * @param approvedBy the identifier of the approver (optional)
     * @return the updated QuotationDto object
     */
    @PreAuthorize("hasAnyAuthority('quotation.approve', 'ROLE_ADMIN', 'ROLE_MANAGER')") // Restrict to managers/admins
    @PutMapping("/{id}/status")
    public ResponseEntity<QuotationDto> updateQuotationStatus(@PathVariable UUID id, @RequestParam String status, @RequestParam(required = false) String approvedBy) {
        return ResponseEntity.ok(quotationService.updateQuotationStatus(id, status, approvedBy));
    }

    /**
     * Deletes a quotation by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the quotation to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable UUID id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}
