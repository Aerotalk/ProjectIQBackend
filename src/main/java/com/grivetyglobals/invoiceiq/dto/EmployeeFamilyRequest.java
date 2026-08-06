package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for a single family/nominee record.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFamilyRequest {

    private String name;
    private String relationship;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private Boolean dependent;
    private Boolean nominee;
    private BigDecimal nomineePercentage;
}
