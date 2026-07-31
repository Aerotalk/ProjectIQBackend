package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for DesignationDto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignationDto {
    private UUID id;
    private UUID organizationId;
    private String designationCode;
    private String designationName;
    private UUID roleId;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
