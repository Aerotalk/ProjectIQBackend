package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id")
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    @Column(name = "stored_name", length = 255, nullable = false)
    private String storedName;

    @Column(name = "mime_type", length = 100, nullable = false)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_path", columnDefinition = "TEXT", nullable = false)
    private String storagePath;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @CreatedDate
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
}
