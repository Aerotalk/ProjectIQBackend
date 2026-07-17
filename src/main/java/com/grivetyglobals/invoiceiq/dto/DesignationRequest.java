package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesignationRequest {

    @NotBlank(message = "Designation Code is required")
    private String designationCode;

    @NotBlank(message = "Designation Name is required")
    private String designationName;

    private Integer hierarchyLevel;

    private UUID companyId;

    private String description;
}
