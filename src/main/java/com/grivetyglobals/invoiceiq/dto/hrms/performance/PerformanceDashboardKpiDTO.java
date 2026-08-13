package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDashboardKpiDTO {
    private long activeCycles;
    private long pendingSelf;
    private long pendingManager;
    private long completedReviews;
    private double averageRating;
    
    private List<TopGoalDTO> topGoals;
    private List<CycleStatusDTO> cycleStatuses;
    private List<DepartmentRatingDTO> departmentRatings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopGoalDTO {
        private String id;
        private String title;
        private Integer progress;
        private String employeeName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CycleStatusDTO {
        private String name;
        private Integer value; // completion percentage
    }
}
