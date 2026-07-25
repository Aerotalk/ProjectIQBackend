package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.VendorDto;
import com.grivetyglobals.invoiceiq.service.sales.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing vendors in the sales/finance module.
 * Provides endpoints for creating, reading, updating, and deleting vendors.
 */
@RestController
@RequestMapping("/api/admin/sales/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    /**
     * Retrieves all vendors for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of VendorDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<VendorDto>> getVendors(@RequestParam UUID companyId) {
        return ResponseEntity.ok(vendorService.getVendorsByCompany(companyId));
    }

    /**
     * Retrieves a specific vendor by their UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the vendor
     * @return the VendorDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendor(@PathVariable UUID id) {
        return ResponseEntity.ok(vendorService.getVendor(id));
    }

    /**
     * Creates a new vendor for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the vendor data payload
     * @return the created VendorDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<VendorDto> createVendor(@RequestParam UUID companyId, @RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.createVendor(companyId, dto));
    }

    /**
     * Updates an existing vendor.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the vendor to update
     * @param dto the updated vendor data payload
     * @return the updated VendorDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<VendorDto> updateVendor(@PathVariable UUID id, @RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.updateVendor(id, dto));
    }

    /**
     * Deletes a vendor by their UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the vendor to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
}
