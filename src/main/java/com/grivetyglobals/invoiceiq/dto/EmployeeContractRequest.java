package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for saving an employee's employment contract.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeContractRequest {

    private String contractType;
    private String contractStartDate;
    private String contractEndDate;
    private BigDecimal contractAnnualCTC;
    private Integer contractNoticePeriod;
    private String contractTerms;
    /** UUID from files table for signed contract document — optional */
    private UUID signedContractFileId;
}
