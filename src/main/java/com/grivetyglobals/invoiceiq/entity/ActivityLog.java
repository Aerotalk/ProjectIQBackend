package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action; // e.g., "LOGIN", "COMPANY_UPDATE"

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "entity_id")
    private UUID entityId; // The ID of the affected resource

    @Column(name = "entity_type")
    private String entityType; // e.g., "Company", "Employee"

    @Column(name = "user_id", nullable = false)
    private UUID userId; // Who performed the action

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId; // For scoped queries

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
