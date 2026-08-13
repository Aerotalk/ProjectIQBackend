package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewRequest {
    
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Cycle ID is required")
    private UUID cycleId;
    
    // For self review
    private String strengths;
    private String areasOfImprovement;
    
    // For manager review
    private String promotionRecommendation;
    private String trainingRecommendation;
    private String improvementPlan;
    private String managerComments;
    
    // Common
    private BigDecimal overallRating;
    private List<GoalRatingSubmitDTO> goalAchievement;
    private List<CompetencyRatingSubmitDTO> competencyRatings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalRatingSubmitDTO {
        private UUID goalId;
        private BigDecimal rating;
        private String comments;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetencyRatingSubmitDTO {
        private UUID competencyId;
        private BigDecimal rating;
        private String comments;
    }
}
