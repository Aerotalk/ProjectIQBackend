package com.grivetyglobals.invoiceiq.dto.hrms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunDetailComponentDto {
    private String componentName;
    private String type; // "EARNING" | "DEDUCTION" | "REIMBURSEMENT"
    private BigDecimal amount;
}
