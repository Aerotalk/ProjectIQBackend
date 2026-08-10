package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for saving an employee separation / exit record.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSeparationRequest {

    private String separationType;
    private String resignationDate;
    private String lastWorkingDate;
    private Integer exitNoticePeriod;
    private String separationReason;
    private Boolean exitInterview;
    private String separationRemarks;
}
