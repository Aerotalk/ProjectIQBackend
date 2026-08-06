package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for saving an employee's present and permanent addresses.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAddressRequest {

    // Present Address
    private String presentCountry;
    private String presentState;
    private String presentCity;
    private String presentAddressLine1;
    private String presentAddressLine2;
    private String presentPinCode;
    private String presentPhone;

    // Permanent Address
    private String permanentCountry;
    private String permanentState;
    private String permanentCity;
    private String permanentAddressLine1;
    private String permanentAddressLine2;
    private String permanentPinCode;
    private String permanentPhone;
}
