package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.AppraisalCycle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppraisalCycleDTO {
    private UUID id;
    private String name;
    private String type;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate selfReviewDeadline;
    private LocalDate managerReviewDeadline;
    private LocalDate hrReviewDeadline;
    private String status;
    private Integer eligibleCount;
    private Integer completionPercentage;
    private String description;
    
    // Additional fields could be added here if needed, like target departments

    public static AppraisalCycleDTO fromEntity(AppraisalCycle cycle) {
        if (cycle == null) {
            return null;
        }
        
        return AppraisalCycleDTO.builder()
                .id(cycle.getId())
                .name(cycle.getName())
                .type(cycle.getType())
                .period(cycle.getPeriod())
                .startDate(cycle.getStartDate())
                .endDate(cycle.getEndDate())
                .selfReviewDeadline(cycle.getSelfReviewDeadline())
                .managerReviewDeadline(cycle.getManagerReviewDeadline())
                .hrReviewDeadline(cycle.getHrReviewDeadline())
                .status(cycle.getStatus())
                .eligibleCount(cycle.getEligibleCount())
                .completionPercentage(cycle.getCompletionPercentage())
                .description(cycle.getDescription())
                .build();
    }
}
