package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private UUID id;
    private UUID organizationId;
    private String departmentCode;
    private String departmentName;
    private UUID parentDepartmentId;
    private String parentDepartmentName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
