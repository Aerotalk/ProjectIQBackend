package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.entity.File;
import com.grivetyglobals.invoiceiq.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final S3Client s3Client;

    @Value("${cloud.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public File uploadFile(MultipartFile multipartFile, UUID uploadedBy, String module) {
        try {
            UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
            if (multipartFile.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Generate unique filename to prevent collisions
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            String orgPath = organizationId != null ? organizationId.toString() : "system";
            String safeModule = (module != null && !module.trim().isEmpty()) ? module.trim().toLowerCase() : "general";
            String yearMonth = java.time.YearMonth.now().toString(); // e.g. 2026-07
            String storagePath = orgPath + "/" + safeModule + "/" + yearMonth + "/" + storedFilename;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

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
            throw new RuntimeException("Failed to store file in S3.", e);
        }
    }

    public Resource loadFileAsResource(UUID fileId) {
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileEntity.getStoragePath())
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(s3Object);
        } catch (Exception e) {
            throw new RuntimeException("Could not read file from S3: " + fileEntity.getOriginalName(), e);
        }
    }

    public File getFileMetadata(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
