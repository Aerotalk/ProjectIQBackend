package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.AuditLog;
import com.grivetyglobals.invoiceiq.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PreAuthorize("hasAuthority('org.view')")
    @GetMapping
    public ResponseEntity<Page<AuditLog>> getPaginatedActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return ResponseEntity.ok(auditService.getPaginatedActivity(PageRequest.of(page, size)));
    }
}
