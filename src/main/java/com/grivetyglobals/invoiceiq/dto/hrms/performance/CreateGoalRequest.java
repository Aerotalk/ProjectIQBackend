package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoalRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Cycle ID is required")
    private UUID cycleId;
    
    private String category;
    private Integer weightage;
    private String kpi;
    
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private String unit;
    
    private LocalDate dueDate;
    private String priority;
    private String status;
    private Integer progress;
    private String comments;
}
