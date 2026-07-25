package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PaymentDto;
import com.grivetyglobals.invoiceiq.service.finance.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing payments in the finance module.
 * Provides endpoints for creating, reading, updating, and deleting payments.
 */
@RestController
@RequestMapping("/api/admin/finance/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Retrieves all payments for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of PaymentDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@RequestParam UUID companyId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCompany(companyId));
    }

    /**
     * Retrieves a specific payment by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the payment
     * @return the PaymentDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    /**
     * Creates a new payment for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the payment data payload
     * @return the created PaymentDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestParam UUID companyId, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.createPayment(companyId, dto));
    }

    /**
     * Updates an existing payment.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the payment to update
     * @param dto the updated payment data payload
     * @return the updated PaymentDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    /**
     * Deletes a payment by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the payment to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
