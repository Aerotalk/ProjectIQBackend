package com.grivetyglobals.invoiceiq.controller.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.KBDto;
import com.grivetyglobals.invoiceiq.service.ticket.KBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Knowledge Base (KB) articles.
 * Provides endpoints for creating, reading, updating, and deleting KB articles.
 */
@RestController
@RequestMapping("/api/admin/kb")
@RequiredArgsConstructor
public class KBController {

    private final KBService kbService;

    /**
     * Retrieves all Knowledge Base articles for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of KBDto objects representing the articles
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<KBDto>> getArticles(@RequestParam UUID companyId) {
        return ResponseEntity.ok(kbService.getArticlesByCompany(companyId));
    }

    /**
     * Retrieves a specific Knowledge Base article by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the article
     * @return the KBDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<KBDto> getArticle(@PathVariable UUID id) {
        return ResponseEntity.ok(kbService.getArticle(id));
    }

    /**
     * Creates a new Knowledge Base article for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the article data payload
     * @return the created KBDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<KBDto> createArticle(@RequestParam UUID companyId, @RequestBody KBDto dto) {
        return ResponseEntity.ok(kbService.createArticle(companyId, dto));
    }

    /**
     * Updates an existing Knowledge Base article.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the article to update
     * @param dto the updated article data payload
     * @return the updated KBDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<KBDto> updateArticle(@PathVariable UUID id, @RequestBody KBDto dto) {
        return ResponseEntity.ok(kbService.updateArticle(id, dto));
    }

    /**
     * Deletes a Knowledge Base article by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the article to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        kbService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
