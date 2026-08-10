package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for recording an employee position change event.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePositionChangeRequest {

    private String positionChangeType;
    private String positionChangeEffectiveDate;
    private String positionChangeDepartmentId;
    private String positionChangeDesignationId;
    private String positionChangeGrade;
    private String positionChangeLocation;
    private UUID positionChangeReportingManagerId;
    private String positionChangeRemarks;
}
