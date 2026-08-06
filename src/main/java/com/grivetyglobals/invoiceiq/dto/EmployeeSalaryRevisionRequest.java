package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for recording a salary revision event.
 * Each submission creates a new historical record (append-only).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSalaryRevisionRequest {

    private String revisionType;
    private String effectiveDate;
    private BigDecimal annualCTC;
    private BigDecimal incrementPercentage;
    private String salaryComponents;
    private String reason;
}
