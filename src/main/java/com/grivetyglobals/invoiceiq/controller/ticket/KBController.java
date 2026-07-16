package com.grivetyglobals.invoiceiq.controller.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.KBDto;
import com.grivetyglobals.invoiceiq.service.ticket.KBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/kb")
@RequiredArgsConstructor
public class KBController {

    private final KBService kbService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<KBDto>> getArticles(@RequestParam UUID companyId) {
        return ResponseEntity.ok(kbService.getArticlesByCompany(companyId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<KBDto> getArticle(@PathVariable UUID id) {
        return ResponseEntity.ok(kbService.getArticle(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<KBDto> createArticle(@RequestParam UUID companyId, @RequestBody KBDto dto) {
        return ResponseEntity.ok(kbService.createArticle(companyId, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<KBDto> updateArticle(@PathVariable UUID id, @RequestBody KBDto dto) {
        return ResponseEntity.ok(kbService.updateArticle(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        kbService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
