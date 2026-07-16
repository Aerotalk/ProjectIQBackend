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
    private String subject;
    private String description;
    private String client;
    private String clientContact;
    private UUID projectId;
    private String category;
    private String priority;
    private String status;
    private String assigned;
    private String supportingMember;
    private String finalRemarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
