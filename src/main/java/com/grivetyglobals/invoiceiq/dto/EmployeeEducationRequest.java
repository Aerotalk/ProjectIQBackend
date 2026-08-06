package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for a single education history record.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEducationRequest {

    private String degree;
    private String qualification;
    private String institution;
    private String fieldOfStudy;
    private String startYear;
    private String endYear;
    private String grade;
}
