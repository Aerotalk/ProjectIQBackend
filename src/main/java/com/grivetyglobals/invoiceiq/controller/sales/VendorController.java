package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.VendorDto;
import com.grivetyglobals.invoiceiq.service.sales.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sales/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<VendorDto>> getVendors(@RequestParam UUID companyId) {
        return ResponseEntity.ok(vendorService.getVendorsByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendor(@PathVariable UUID id) {
        return ResponseEntity.ok(vendorService.getVendor(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<VendorDto> createVendor(@RequestParam UUID companyId, @RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.createVendor(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<VendorDto> updateVendor(@PathVariable UUID id, @RequestBody VendorDto dto) {
        return ResponseEntity.ok(vendorService.updateVendor(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }
}
