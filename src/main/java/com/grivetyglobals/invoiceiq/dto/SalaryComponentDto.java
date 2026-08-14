package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for an individual Salary Component in a Revision.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryComponentDto {
    private String componentName;
    private String type; // "EARNING", "DEDUCTION", "REIMBURSEMENT"
    private BigDecimal percentage; // percentage of CTC (if applicable)
    private BigDecimal amount; // flat annual amount (if applicable)
}
