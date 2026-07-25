package com.grivetyglobals.invoiceiq.controller.sales;

import com.grivetyglobals.invoiceiq.dto.sales.ProductDto;
import com.grivetyglobals.invoiceiq.service.sales.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing products/services in the sales module.
 * Provides endpoints for creating, reading, updating, and deleting products.
 */
@RestController
@RequestMapping("/api/admin/sales/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Retrieves all products for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of ProductDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam UUID companyId) {
        return ResponseEntity.ok(productService.getProductsByCompany(companyId));
    }

    /**
     * Retrieves a specific product by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the product
     * @return the ProductDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    /**
     * Creates a new product for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the product data payload
     * @return the created ProductDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestParam UUID companyId, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.createProduct(companyId, dto));
    }

    /**
     * Updates an existing product.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the product to update
     * @param dto the updated product data payload
     * @return the updated ProductDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    /**
     * Deletes a product by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the product to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
