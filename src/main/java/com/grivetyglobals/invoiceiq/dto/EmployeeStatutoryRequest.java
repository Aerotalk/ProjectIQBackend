package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for saving an employee's statutory/compliance details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatutoryRequest {

    private String panNumber;
    private String aadhaarNumber;
    private String uan;
    private String pfNumber;
    private String esiNumber;
    private String passportNumber;
    private String passportExpiry;
    private String voterId;
    private String drivingLicense;
    private String drivingLicenseExpiry;
    private Boolean pfApplicable;
    private Boolean esiApplicable;
    private String taxRegime;
}
