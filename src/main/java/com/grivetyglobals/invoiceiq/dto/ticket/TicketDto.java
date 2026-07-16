package com.grivetyglobals.invoiceiq.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private UUID id;
    private String ticketNo;
    private UUID projectId;
    private String shortDescription;
    private String description;
    private String module;
    private String category;
    private String subCategory;
    private String assignmentGroup;
    private String assignedTo;
    private String reportedBy;
    private String contactEmail;
    private String contactNumber;
    private String customerCompany;
    private String organization;
    private String location;
    private String environment;
    private String impact;
    private String urgency;
    private String priority;
    private String state;
    private String incidentType;
    private String source;
    private String severity;
    private String slaPolicy;
    private LocalDateTime dueDate;
    private String resolutionCode;
    private String resolutionNotes;
    private String rootCause;
    private String workNotes;
    private String customerComments;
    private String relatedChange;
    private String relatedProblem;
    private String parentIncident;
    private String duplicateOf;
    private String watchList;
    private String browser;
    private String operatingSystem;
    private String deviceType;
    private String ipAddress;
    private LocalDateTime resolvedOn;
    private LocalDateTime closedOn;
    private String closedBy;
    private Integer reopenCount;
    private String escalationLevel;
    private String tags;
    private String knowledgeArticle;
    private String businessService;
    private String configurationItem;
    private Boolean isMajorIncident;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
