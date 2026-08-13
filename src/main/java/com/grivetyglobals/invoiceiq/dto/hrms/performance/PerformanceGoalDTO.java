package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.PerformanceGoal;
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
public class PerformanceGoalDTO {
    private UUID id;
    private String title;
    private String description;
    
    private EmployeeSummaryDTO employee;
    
    private UUID cycleId;
    private String cycleName;
    
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

    public static PerformanceGoalDTO fromEntity(PerformanceGoal goal) {
        if (goal == null) {
            return null;
        }

        UUID cycleId = null;
        String cycleName = null;
        if (goal.getCycle() != null) {
            cycleId = goal.getCycle().getId();
            cycleName = goal.getCycle().getName();
        }

        return PerformanceGoalDTO.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .employee(EmployeeSummaryDTO.fromEntity(goal.getEmployee()))
                .cycleId(cycleId)
                .cycleName(cycleName)
                .category(goal.getCategory())
                .weightage(goal.getWeightage())
                .kpi(goal.getKpi())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .dueDate(goal.getDueDate())
                .priority(goal.getPriority())
                .status(goal.getStatus())
                .progress(goal.getProgress())
                .comments(goal.getComments())
                .build();
    }
}
