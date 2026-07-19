package com.grivetyglobals.invoiceiq.controller.settings;

import com.grivetyglobals.invoiceiq.dto.DocumentTemplateDto;
import com.grivetyglobals.invoiceiq.entity.DocumentTemplate;
import com.grivetyglobals.invoiceiq.repository.DocumentTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class DocumentTemplateController {

    private final DocumentTemplateRepository documentTemplateRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<DocumentTemplateDto>> getTemplates(@RequestParam(required = false) String type) {
        List<DocumentTemplate> templates;
        
        if (type != null && !type.isBlank()) {
            templates = documentTemplateRepository.findByTypeContainingIgnoreCase(type);
        } else {
            templates = documentTemplateRepository.findAll();
        }

        List<DocumentTemplateDto> dtos = templates.stream()
                .map(t -> DocumentTemplateDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .filename(t.getFilename())
                        .type(t.getType())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{filename}")
    public ResponseEntity<String> getTemplateContent(@PathVariable String filename) {
        Optional<DocumentTemplate> templateOpt = documentTemplateRepository.findByFilename(filename);
        
        if (templateOpt.isEmpty()) {
            // Also try to find by ID in case the frontend sends the UUID as filename by mistake
            try {
                java.util.UUID uuid = java.util.UUID.fromString(filename);
                templateOpt = documentTemplateRepository.findById(uuid);
            } catch (IllegalArgumentException e) {
                // Ignore, it's not a UUID
            }
        }
        
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found");
        }

        return ResponseEntity.ok(templateOpt.get().getContent());
    }
}
