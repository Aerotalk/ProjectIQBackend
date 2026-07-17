package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.entity.File;
import com.grivetyglobals.invoiceiq.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file, 
                                           @RequestParam(value = "module", required = false) String module,
                                           @AuthenticationPrincipal com.grivetyglobals.invoiceiq.entity.User user) {
        UUID uploadedBy = (user != null) ? user.getId() : null;
        return ResponseEntity.ok(fileService.uploadFile(file, uploadedBy, module));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> downloadFile(@PathVariable UUID fileId) {
        File metadata = fileService.getFileMetadata(fileId);
        java.io.InputStream inputStream = fileService.getFileInputStream(fileId);

        org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody responseBody = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getMimeType()))
                .contentLength(metadata.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + metadata.getOriginalName() + "\"")
                .body(responseBody);
    }
}
