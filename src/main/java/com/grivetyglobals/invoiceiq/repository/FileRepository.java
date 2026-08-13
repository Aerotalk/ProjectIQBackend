package com.grivetyglobals.invoiceiq.repository;

import com.grivetyglobals.invoiceiq.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for FileRepository.
 */
@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findAllByUploadedByOrderByUploadedAtDesc(UUID uploadedBy);
    List<File> findAllByUploadedByAndStoragePathContainingOrderByUploadedAtDesc(UUID uploadedBy, String module);
}
