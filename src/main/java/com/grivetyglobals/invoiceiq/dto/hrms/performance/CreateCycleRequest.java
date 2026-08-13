package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCycleRequest {

    @NotBlank(message = "Name is required")
    private String name;
    
    private String type;
    private String period;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private LocalDate selfReviewDeadline;
    private LocalDate managerReviewDeadline;
    private LocalDate hrReviewDeadline;
    
    private String status;
    private String description;
    
    private List<String> targetDepartments;
    private List<String> targetLocations;
}
