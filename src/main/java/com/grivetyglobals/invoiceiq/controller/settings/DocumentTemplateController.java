package com.grivetyglobals.invoiceiq.controller.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class DocumentTemplateController {

    private static final String TEMPLATES_DIR = "Document_Templates";

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<String>> getTemplates(@RequestParam(required = false) String type) {
        Path dirPath = Paths.get(TEMPLATES_DIR);
        if (!Files.exists(dirPath)) {
            return ResponseEntity.ok(List.of());
        }

        try (Stream<Path> stream = Files.list(dirPath)) {
            List<String> files = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".html"))
                    .filter(name -> type == null || name.toLowerCase().contains(type.toLowerCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read templates directory", e);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{filename}")
    public ResponseEntity<String> getTemplateContent(@PathVariable String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new RuntimeException("Invalid filename");
        }

        Path filePath = Paths.get(TEMPLATES_DIR, filename);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Template not found");
        }

        try {
            String content = Files.readString(filePath);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template content", e);
        }
    }
}
