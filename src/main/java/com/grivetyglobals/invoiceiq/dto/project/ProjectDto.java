package com.grivetyglobals.invoiceiq.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private UUID id;
    private String projectCode;
    private String projectName;
    private String description;
    private String status;
    private String client;
    private String projectManager;
    private String linkedQuotation;
    private String startDate;
    private String expectedEndDate;
    private Double expectedRevenue;

    private java.util.List<String> assignedVendors;
    private java.util.List<String> assignedEntities;
    private java.util.List<String> linkedIncidents;
    private java.util.List<String> linkedQuotations;
    private java.util.List<String> linkedPOs;
    private java.util.List<String> linkedExpenses;
}
