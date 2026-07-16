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
                .subject(entity.getSubject())
                .description(entity.getDescription())
                .client(entity.getClient())
                .clientContact(entity.getClientContact())
                .projectId(entity.getProject() != null ? entity.getProject().getId() : null)
                .category(entity.getCategory())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .assigned(entity.getAssigned())
                .supportingMember(entity.getSupportingMember())
                .finalRemarks(entity.getFinalRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Ticket toEntity(TicketDto dto) {
        if (dto == null) return null;

        Ticket ticket = Ticket.builder()
                .id(dto.getId())
                .ticketNo(dto.getTicketNo())
                .subject(dto.getSubject())
                .description(dto.getDescription())
                .client(dto.getClient())
                .clientContact(dto.getClientContact())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .assigned(dto.getAssigned())
                .supportingMember(dto.getSupportingMember())
                .finalRemarks(dto.getFinalRemarks())
                .build();

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            ticket.setProject(project);
        }

        return ticket;
    }

    public void updateEntityFromDto(TicketDto dto, Ticket entity) {
        if (dto.getSubject() != null) entity.setSubject(dto.getSubject());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getClient() != null) entity.setClient(dto.getClient());
        if (dto.getClientContact() != null) entity.setClientContact(dto.getClientContact());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getPriority() != null) entity.setPriority(dto.getPriority());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getAssigned() != null) entity.setAssigned(dto.getAssigned());
        if (dto.getSupportingMember() != null) entity.setSupportingMember(dto.getSupportingMember());
        if (dto.getFinalRemarks() != null) entity.setFinalRemarks(dto.getFinalRemarks());
        
        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            entity.setProject(project);
        } else {
            entity.setProject(null);
        }
    }
}
