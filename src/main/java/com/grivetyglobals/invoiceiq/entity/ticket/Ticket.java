package com.grivetyglobals.invoiceiq.entity.ticket;

import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Tenant isolation

    @Column(name = "ticket_no", length = 50, nullable = false)
    private String ticketNo; // Incident ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // The only other required field per user

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String module;

    @Column(length = 100)
    private String category;

    @Column(name = "sub_category", length = 100)
    private String subCategory;

    @Column(name = "assignment_group", length = 100)
    private String assignmentGroup;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "reported_by", length = 100)
    private String reportedBy;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(name = "customer_company", length = 255)
    private String customerCompany;

    @Column(length = 100)
    private String organization;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String environment;

    @Column(length = 50)
    private String impact;

    @Column(length = 50)
    private String urgency;

    @Column(length = 50)
    private String priority;

    @Column(length = 50)
    private String state;

    @Column(name = "incident_type", length = 50)
    private String incidentType;

    @Column(length = 50)
    private String source;

    @Column(length = 50)
    private String severity;

    @Column(name = "sla_policy", length = 100)
    private String slaPolicy;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "resolution_code", length = 100)
    private String resolutionCode;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "work_notes", columnDefinition = "TEXT")
    private String workNotes;

    @Column(name = "customer_comments", columnDefinition = "TEXT")
    private String customerComments;

    @Column(name = "related_change", length = 100)
    private String relatedChange;

    @Column(name = "related_problem", length = 100)
    private String relatedProblem;

    @Column(name = "parent_incident", length = 100)
    private String parentIncident;

    @Column(name = "duplicate_of", length = 100)
    private String duplicateOf;

    @Column(name = "watch_list", columnDefinition = "TEXT")
    private String watchList; // JSON or CSV

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "device_type", length = 100)
    private String deviceType;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "resolved_on")
    private LocalDateTime resolvedOn;

    @Column(name = "closed_on")
    private LocalDateTime closedOn;

    @Column(name = "closed_by", length = 100)
    private String closedBy;

    @Column(name = "reopen_count")
    private Integer reopenCount = 0;

    @Column(name = "escalation_level", length = 50)
    private String escalationLevel;

    @Column(columnDefinition = "TEXT")
    private String tags; // JSON or CSV

    @Column(name = "knowledge_article", length = 100)
    private String knowledgeArticle;

    @Column(name = "business_service", length = 100)
    private String businessService;

    @Column(name = "configuration_item", length = 100)
    private String configurationItem;

    @Column(name = "is_major_incident")
    private Boolean isMajorIncident = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
