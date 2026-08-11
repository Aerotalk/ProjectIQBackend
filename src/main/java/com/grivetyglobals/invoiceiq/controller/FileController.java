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

/**
 * REST controller for file uploading and downloading operations.
 * Handles multipart file uploads and streaming file downloads.
 */
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Uploads a file to the system storage.
     * Requires the user to be authenticated.
     *
     * @param file   the multipart file to upload
     * @param module an optional module identifier (e.g., 'profile_pictures')
     * @param user   the authenticated user performing the upload
     * @return the saved File entity metadata
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file, 
                                           @RequestParam(value = "module", required = false) String module,
                                           @AuthenticationPrincipal com.grivetyglobals.invoiceiq.entity.User user) {
        UUID uploadedBy = (user != null) ? user.getId() : null;
        return ResponseEntity.ok(fileService.uploadFile(file, uploadedBy, module));
    }

    /**
     * Retrieves all files uploaded by the authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-files")
    public ResponseEntity<java.util.List<File>> getMyFiles(@AuthenticationPrincipal com.grivetyglobals.invoiceiq.entity.User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(fileService.getUserFiles(user.getId()));
    }

    /**
     * Downloads a file from the system storage via a streaming response.
     * Requires the user to be authenticated.
     *
     * @param fileIdStr the UUID of the file as a string
     * @return a streaming response containing the file data
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileIdStr}")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> downloadFile(@PathVariable String fileIdStr) {
        UUID fileId;
        try {
            fileId = UUID.fromString(fileIdStr.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid file ID format");
        }
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
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") // Cache for 24 hours
                .body(responseBody);
    }
}
