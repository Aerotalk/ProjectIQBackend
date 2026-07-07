package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.File;
import com.grivetyglobals.invoiceiq.repository.FileRepository;
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

    private final FileRepository fileRepository;
    private final Path rootLocation = Paths.get("uploads");

    @Transactional
    public File uploadFile(MultipartFile multipartFile, UUID organizationId, UUID uploadedBy) {
        try {
            if (multipartFile.isEmpty()) {
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
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            Path destinationFile = orgDir.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(orgDir.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            Files.copy(multipartFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            String storagePath = organizationId.toString() + "/" + storedFilename;
            
            File file = File.builder()
                    .originalName(originalFilename)
                    .storedName(storedFilename)
                    .mimeType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .storagePath(storagePath)
                    .organizationId(organizationId)
                    .uploadedBy(uploadedBy)
                    .build();

            return fileRepository.save(file);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public Resource loadFileAsResource(UUID fileId) {
        try {
            File fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            Path file = rootLocation.resolve(fileEntity.getStoragePath()).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + fileEntity.getOriginalName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file.", e);
        }
    }

    public File getFileMetadata(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
