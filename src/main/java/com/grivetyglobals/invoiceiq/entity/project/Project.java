package com.grivetyglobals.invoiceiq.entity.project;

import com.grivetyglobals.invoiceiq.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "project_code", nullable = false, length = 50)
    private String projectCode;

    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String status;

    @Column(name = "client", length = 200)
    private String client;

    @Column(name = "project_manager", length = 200)
    private String projectManager;

    @Column(name = "linked_quotation", length = 100)
    private String linkedQuotation;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "expected_end_date")
    private String expectedEndDate;

    @Column(name = "expected_revenue")
    private Double expectedRevenue;

    @ElementCollection
    @CollectionTable(name = "project_vendors", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "vendor_id")
    private java.util.List<String> assignedVendors;

    @ElementCollection
    @CollectionTable(name = "project_entities", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "entity_id")
    private java.util.List<String> assignedEntities;

    @ElementCollection
    @CollectionTable(name = "project_incidents", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "incident_id")
    private java.util.List<String> linkedIncidents;

    @ElementCollection
    @CollectionTable(name = "project_quotations", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "quotation_id")
    private java.util.List<String> linkedQuotations;

    @ElementCollection
    @CollectionTable(name = "project_pos", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "po_id")
    private java.util.List<String> linkedPOs;

    @ElementCollection
    @CollectionTable(name = "project_expenses", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "expense_id")
    private java.util.List<String> linkedExpenses;

    @ElementCollection
    @CollectionTable(name = "project_notes", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "note", columnDefinition = "TEXT")
    private java.util.List<String> projectNotes;

    @ElementCollection
    @CollectionTable(name = "project_documents", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "document_json", columnDefinition = "TEXT")
    private java.util.List<String> projectDocuments;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
