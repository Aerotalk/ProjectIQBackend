package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ChallanDto;
import com.grivetyglobals.invoiceiq.service.finance.ChallanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing delivery challans in the finance module.
 * Provides endpoints for creating, reading, updating, and deleting challans.
 */
@RestController
@RequestMapping("/api/admin/finance/challans")
@RequiredArgsConstructor
public class ChallanController {

    private final ChallanService challanService;

    /**
     * Retrieves all challans for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of ChallanDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ChallanDto>> getChallans(@RequestParam UUID companyId) {
        return ResponseEntity.ok(challanService.getChallansByCompany(companyId));
    }

    /**
     * Retrieves a specific challan by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the challan
     * @return the ChallanDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ChallanDto> getChallan(@PathVariable UUID id) {
        return ResponseEntity.ok(challanService.getChallan(id));
    }

    /**
     * Creates a new challan for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the challan data payload
     * @return the created ChallanDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ChallanDto> createChallan(@RequestParam UUID companyId, @RequestBody ChallanDto dto) {
        return ResponseEntity.ok(challanService.createChallan(companyId, dto));
    }

    /**
     * Updates an existing challan.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the challan to update
     * @param dto the updated challan data payload
     * @return the updated ChallanDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ChallanDto> updateChallan(@PathVariable UUID id, @RequestBody ChallanDto dto) {
        return ResponseEntity.ok(challanService.updateChallan(id, dto));
    }

    /**
     * Deletes a challan by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the challan to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallan(@PathVariable UUID id) {
        challanService.deleteChallan(id);
        return ResponseEntity.noContent().build();
    }
}
