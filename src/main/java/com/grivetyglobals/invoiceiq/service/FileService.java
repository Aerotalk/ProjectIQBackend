package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.FileMetadata;
import com.grivetyglobals.invoiceiq.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final Path rootLocation = Paths.get("uploads");

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, UUID organizationId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Create uploads directory if it doesn't exist
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Organization specific directory
            Path orgDir = rootLocation.resolve(organizationId.toString());
            if (!Files.exists(orgDir)) {
                Files.createDirectories(orgDir);
            }

            // Generate unique filename to prevent collisions
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            Path destinationFile = orgDir.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(orgDir.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = FileMetadata.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(organizationId.toString() + "/" + storedFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .organizationId(organizationId)
                    .build();

            return fileMetadataRepository.save(metadata);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public Resource loadFileAsResource(UUID fileId) {
        try {
            FileMetadata metadata = fileMetadataRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            Path file = rootLocation.resolve(metadata.getStoredFilename()).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + metadata.getOriginalFilename());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file.", e);
        }
    }

    public FileMetadata getFileMetadata(UUID fileId) {
        return fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
