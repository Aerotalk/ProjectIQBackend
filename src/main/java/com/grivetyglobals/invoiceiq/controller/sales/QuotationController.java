package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.QuotationDto;
import com.grivetyglobals.invoiceiq.service.sales.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sales/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<QuotationDto>> getQuotations(@RequestParam UUID companyId) {
        return ResponseEntity.ok(quotationService.getQuotationsByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<QuotationDto> getQuotation(@PathVariable UUID id) {
        return ResponseEntity.ok(quotationService.getQuotation(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<QuotationDto> createQuotation(@RequestParam UUID companyId, @RequestBody QuotationDto dto) {
        return ResponseEntity.ok(quotationService.createQuotation(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<QuotationDto> updateQuotation(@PathVariable UUID id, @RequestBody QuotationDto dto) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable UUID id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}
