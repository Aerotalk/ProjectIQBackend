package com.grivetyglobals.invoiceiq.service.ticket;

import com.grivetyglobals.invoiceiq.dto.ticket.TicketDto;
import com.grivetyglobals.invoiceiq.entity.ticket.Ticket;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket entity) {
        if (entity == null) return null;

        return TicketDto.builder()
                .id(entity.getId())
                .ticketNo(entity.getTicketNo())
                .projectId(entity.getProject() != null ? entity.getProject().getId() : null)
                .shortDescription(entity.getShortDescription())
                .description(entity.getDescription())
                .module(entity.getModule())
                .category(entity.getCategory())
                .subCategory(entity.getSubCategory())
                .assignmentGroup(entity.getAssignmentGroup())
                .assignedTo(entity.getAssignedTo())
                .reportedBy(entity.getReportedBy())
                .contactEmail(entity.getContactEmail())
                .contactNumber(entity.getContactNumber())
                .customerCompany(entity.getCustomerCompany())
                .organization(entity.getOrganization())
                .location(entity.getLocation())
                .environment(entity.getEnvironment())
                .impact(entity.getImpact())
                .urgency(entity.getUrgency())
                .priority(entity.getPriority())
                .state(entity.getState())
                .incidentType(entity.getIncidentType())
                .source(entity.getSource())
                .severity(entity.getSeverity())
                .slaPolicy(entity.getSlaPolicy())
                .dueDate(entity.getDueDate())
                .resolutionCode(entity.getResolutionCode())
                .resolutionNotes(entity.getResolutionNotes())
                .rootCause(entity.getRootCause())
                .workNotes(entity.getWorkNotes())
                .customerComments(entity.getCustomerComments())
                .relatedChange(entity.getRelatedChange())
                .relatedProblem(entity.getRelatedProblem())
                .parentIncident(entity.getParentIncident())
                .duplicateOf(entity.getDuplicateOf())
                .watchList(entity.getWatchList())
                .browser(entity.getBrowser())
                .operatingSystem(entity.getOperatingSystem())
                .deviceType(entity.getDeviceType())
                .ipAddress(entity.getIpAddress())
                .resolvedOn(entity.getResolvedOn())
                .closedOn(entity.getClosedOn())
                .closedBy(entity.getClosedBy())
                .reopenCount(entity.getReopenCount())
                .escalationLevel(entity.getEscalationLevel())
                .tags(entity.getTags())
                .knowledgeArticle(entity.getKnowledgeArticle())
                .businessService(entity.getBusinessService())
                .configurationItem(entity.getConfigurationItem())
                .isMajorIncident(entity.getIsMajorIncident())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Ticket toEntity(TicketDto dto) {
        if (dto == null) return null;

        Ticket ticket = Ticket.builder()
                .id(dto.getId())
                .ticketNo(dto.getTicketNo())
                .shortDescription(dto.getShortDescription())
                .description(dto.getDescription())
                .module(dto.getModule())
                .category(dto.getCategory())
                .subCategory(dto.getSubCategory())
                .assignmentGroup(dto.getAssignmentGroup())
                .assignedTo(dto.getAssignedTo())
                .reportedBy(dto.getReportedBy())
                .contactEmail(dto.getContactEmail())
                .contactNumber(dto.getContactNumber())
                .customerCompany(dto.getCustomerCompany())
                .organization(dto.getOrganization())
                .location(dto.getLocation())
                .environment(dto.getEnvironment())
                .impact(dto.getImpact())
                .urgency(dto.getUrgency())
                .priority(dto.getPriority())
                .state(dto.getState())
                .incidentType(dto.getIncidentType())
                .source(dto.getSource())
                .severity(dto.getSeverity())
                .slaPolicy(dto.getSlaPolicy())
                .dueDate(dto.getDueDate())
                .resolutionCode(dto.getResolutionCode())
                .resolutionNotes(dto.getResolutionNotes())
                .rootCause(dto.getRootCause())
                .workNotes(dto.getWorkNotes())
                .customerComments(dto.getCustomerComments())
                .relatedChange(dto.getRelatedChange())
                .relatedProblem(dto.getRelatedProblem())
                .parentIncident(dto.getParentIncident())
                .duplicateOf(dto.getDuplicateOf())
                .watchList(dto.getWatchList())
                .browser(dto.getBrowser())
                .operatingSystem(dto.getOperatingSystem())
                .deviceType(dto.getDeviceType())
                .ipAddress(dto.getIpAddress())
                .resolvedOn(dto.getResolvedOn())
                .closedOn(dto.getClosedOn())
                .closedBy(dto.getClosedBy())
                .reopenCount(dto.getReopenCount())
                .escalationLevel(dto.getEscalationLevel())
                .tags(dto.getTags())
                .knowledgeArticle(dto.getKnowledgeArticle())
                .businessService(dto.getBusinessService())
                .configurationItem(dto.getConfigurationItem())
                .isMajorIncident(dto.getIsMajorIncident())
                .build();

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            ticket.setProject(project);
        }

        return ticket;
    }

    public void updateEntityFromDto(TicketDto dto, Ticket entity) {
        if (dto.getShortDescription() != null) entity.setShortDescription(dto.getShortDescription());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getModule() != null) entity.setModule(dto.getModule());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getSubCategory() != null) entity.setSubCategory(dto.getSubCategory());
        if (dto.getAssignmentGroup() != null) entity.setAssignmentGroup(dto.getAssignmentGroup());
        if (dto.getAssignedTo() != null) entity.setAssignedTo(dto.getAssignedTo());
        if (dto.getReportedBy() != null) entity.setReportedBy(dto.getReportedBy());
        if (dto.getContactEmail() != null) entity.setContactEmail(dto.getContactEmail());
        if (dto.getContactNumber() != null) entity.setContactNumber(dto.getContactNumber());
        if (dto.getCustomerCompany() != null) entity.setCustomerCompany(dto.getCustomerCompany());
        if (dto.getOrganization() != null) entity.setOrganization(dto.getOrganization());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getEnvironment() != null) entity.setEnvironment(dto.getEnvironment());
        if (dto.getImpact() != null) entity.setImpact(dto.getImpact());
        if (dto.getUrgency() != null) entity.setUrgency(dto.getUrgency());
        if (dto.getPriority() != null) entity.setPriority(dto.getPriority());
        if (dto.getState() != null) entity.setState(dto.getState());
        if (dto.getIncidentType() != null) entity.setIncidentType(dto.getIncidentType());
        if (dto.getSource() != null) entity.setSource(dto.getSource());
        if (dto.getSeverity() != null) entity.setSeverity(dto.getSeverity());
        if (dto.getSlaPolicy() != null) entity.setSlaPolicy(dto.getSlaPolicy());
        if (dto.getDueDate() != null) entity.setDueDate(dto.getDueDate());
        if (dto.getResolutionCode() != null) entity.setResolutionCode(dto.getResolutionCode());
        if (dto.getResolutionNotes() != null) entity.setResolutionNotes(dto.getResolutionNotes());
        if (dto.getRootCause() != null) entity.setRootCause(dto.getRootCause());
        if (dto.getWorkNotes() != null) entity.setWorkNotes(dto.getWorkNotes());
        if (dto.getCustomerComments() != null) entity.setCustomerComments(dto.getCustomerComments());
        if (dto.getRelatedChange() != null) entity.setRelatedChange(dto.getRelatedChange());
        if (dto.getRelatedProblem() != null) entity.setRelatedProblem(dto.getRelatedProblem());
        if (dto.getParentIncident() != null) entity.setParentIncident(dto.getParentIncident());
        if (dto.getDuplicateOf() != null) entity.setDuplicateOf(dto.getDuplicateOf());
        if (dto.getWatchList() != null) entity.setWatchList(dto.getWatchList());
        if (dto.getBrowser() != null) entity.setBrowser(dto.getBrowser());
        if (dto.getOperatingSystem() != null) entity.setOperatingSystem(dto.getOperatingSystem());
        if (dto.getDeviceType() != null) entity.setDeviceType(dto.getDeviceType());
        if (dto.getIpAddress() != null) entity.setIpAddress(dto.getIpAddress());
        if (dto.getResolvedOn() != null) entity.setResolvedOn(dto.getResolvedOn());
        if (dto.getClosedOn() != null) entity.setClosedOn(dto.getClosedOn());
        if (dto.getClosedBy() != null) entity.setClosedBy(dto.getClosedBy());
        if (dto.getReopenCount() != null) entity.setReopenCount(dto.getReopenCount());
        if (dto.getEscalationLevel() != null) entity.setEscalationLevel(dto.getEscalationLevel());
        if (dto.getTags() != null) entity.setTags(dto.getTags());
        if (dto.getKnowledgeArticle() != null) entity.setKnowledgeArticle(dto.getKnowledgeArticle());
        if (dto.getBusinessService() != null) entity.setBusinessService(dto.getBusinessService());
        if (dto.getConfigurationItem() != null) entity.setConfigurationItem(dto.getConfigurationItem());
        if (dto.getIsMajorIncident() != null) entity.setIsMajorIncident(dto.getIsMajorIncident());

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            entity.setProject(project);
        } else {
            // entity.setProject(null); // Keep project if not updated
        }
    }
}
