package com.grivetyglobals.invoiceiq.controller.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PurchaseOrderDto;
import com.grivetyglobals.invoiceiq.service.finance.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing purchase orders in the finance module.
 * Provides endpoints for creating, reading, updating, and deleting purchase orders.
 */
@RestController
@RequestMapping("/api/admin/finance/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    /**
     * Retrieves all purchase orders for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of PurchaseOrderDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrders(@RequestParam UUID companyId) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByCompany(companyId));
    }

    /**
     * Retrieves a specific purchase order by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the purchase order
     * @return the PurchaseOrderDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrder(id));
    }

    /**
     * Creates a new purchase order for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the purchase order data payload
     * @return the created PurchaseOrderDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@RequestParam UUID companyId, @RequestBody PurchaseOrderDto dto) {
        return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(companyId, dto));
    }

    /**
     * Updates an existing purchase order.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the purchase order to update
     * @param dto the updated purchase order data payload
     * @return the updated PurchaseOrderDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> updatePurchaseOrder(@PathVariable UUID id, @RequestBody PurchaseOrderDto dto) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(id, dto));
    }

    /**
     * Deletes a purchase order by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the purchase order to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable UUID id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
