package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.SelfReview;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.SelfReviewCompetencyRating;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.SelfReviewGoalRating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfReviewDTO {
    private UUID id;
    private EmployeeSummaryDTO employee;
    
    private CycleSummaryDTO cycle;
    
    private String strengths;
    private String areasOfImprovement;
    private BigDecimal overallRating;
    private String status;
    private LocalDateTime submittedOn;
    
    private List<GoalRatingDTO> goalRatings;
    private List<CompetencyRatingDTO> competencyRatings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CycleSummaryDTO {
        private UUID id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalRatingDTO {
        private UUID id;
        private UUID goalId;
        private String goalTitle;
        private BigDecimal employeeRating;
        private String employeeComment;

        public static GoalRatingDTO fromEntity(SelfReviewGoalRating rating) {
            if (rating == null) return null;
            return GoalRatingDTO.builder()
                    .id(rating.getId())
                    .goalId(rating.getGoal() != null ? rating.getGoal().getId() : null)
                    .goalTitle(rating.getGoal() != null ? rating.getGoal().getTitle() : null)
                    .employeeRating(rating.getEmployeeRating())
                    .employeeComment(rating.getEmployeeComment())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetencyRatingDTO {
        private UUID id;
        private UUID competencyId;
        private String competencyName;
        private BigDecimal employeeRating;
        private String employeeComment;

        public static CompetencyRatingDTO fromEntity(SelfReviewCompetencyRating rating) {
            if (rating == null) return null;
            return CompetencyRatingDTO.builder()
                    .id(rating.getId())
                    .competencyId(rating.getCompetency() != null ? rating.getCompetency().getId() : null)
                    .competencyName(rating.getCompetency() != null ? rating.getCompetency().getName() : null)
                    .employeeRating(rating.getEmployeeRating())
                    .employeeComment(rating.getEmployeeComment())
                    .build();
        }
    }

    public static SelfReviewDTO fromEntity(SelfReview review) {
        if (review == null) {
            return null;
        }

        CycleSummaryDTO cycleSummary = null;
        if (review.getCycle() != null) {
            cycleSummary = CycleSummaryDTO.builder()
                    .id(review.getCycle().getId())
                    .name(review.getCycle().getName())
                    .build();
        }

        List<GoalRatingDTO> goalRatings = null;
        if (review.getGoalRatings() != null) {
            goalRatings = review.getGoalRatings().stream()
                    .map(GoalRatingDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        List<CompetencyRatingDTO> compRatings = null;
        if (review.getCompetencyRatings() != null) {
            compRatings = review.getCompetencyRatings().stream()
                    .map(CompetencyRatingDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        return SelfReviewDTO.builder()
                .id(review.getId())
                .employee(EmployeeSummaryDTO.fromEntity(review.getEmployee()))
                .cycle(cycleSummary)
                .strengths(review.getStrengths())
                .areasOfImprovement(review.getAreasOfImprovement())
                .overallRating(review.getOverallRating())
                .status(review.getStatus())
                .submittedOn(review.getSubmittedOn())
                .goalRatings(goalRatings)
                .competencyRatings(compRatings)
                .build();
    }
}
