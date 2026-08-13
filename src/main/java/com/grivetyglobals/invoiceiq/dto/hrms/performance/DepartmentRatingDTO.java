package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRatingDTO {
    private String department; // Used in some places
    private String name;       // Used in some places
    private Integer totalEmployees;
    private Double avgRating;
    private Double rating;
    private Integer topPerformers;
    private Integer needsImprovement;
}
