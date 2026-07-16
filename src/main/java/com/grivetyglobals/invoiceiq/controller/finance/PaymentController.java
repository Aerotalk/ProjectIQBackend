package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PaymentDto;
import com.grivetyglobals.invoiceiq.service.finance.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/finance/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@RequestParam UUID companyId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestParam UUID companyId, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.createPayment(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
