package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.ManagerReview;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.ManagerReviewCompetencyRating;
import com.grivetyglobals.invoiceiq.entity.hrms.performance.ManagerReviewGoalRating;
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
public class ManagerReviewDTO {
    private UUID id;
    
    private SelfReviewSummaryDTO selfReview;
    private EmployeeSummaryDTO employee;
    private EmployeeSummaryDTO manager;
    private SelfReviewDTO.CycleSummaryDTO cycle;
    
    private String promotionRecommendation;
    private String trainingRecommendation;
    private String improvementPlan;
    private BigDecimal overallRating;
    private String managerComments;
    private String status;
    private LocalDateTime completedOn;
    
    private List<GoalRatingDTO> goalRatings;
    private List<CompetencyRatingDTO> competencyRatings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelfReviewSummaryDTO {
        private UUID id;
        private String status;
        private BigDecimal overallRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalRatingDTO {
        private UUID id;
        private UUID goalId;
        private String goalTitle;
        private BigDecimal managerRating;
        private String managerComment;

        public static GoalRatingDTO fromEntity(ManagerReviewGoalRating rating) {
            if (rating == null) return null;
            return GoalRatingDTO.builder()
                    .id(rating.getId())
                    .goalId(rating.getGoal() != null ? rating.getGoal().getId() : null)
                    .goalTitle(rating.getGoal() != null ? rating.getGoal().getTitle() : null)
                    .managerRating(rating.getManagerRating())
                    .managerComment(rating.getManagerComment())
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
        private BigDecimal managerRating;
        private String managerComment;

        public static CompetencyRatingDTO fromEntity(ManagerReviewCompetencyRating rating) {
            if (rating == null) return null;
            return CompetencyRatingDTO.builder()
                    .id(rating.getId())
                    .competencyId(rating.getCompetency() != null ? rating.getCompetency().getId() : null)
                    .competencyName(rating.getCompetency() != null ? rating.getCompetency().getName() : null)
                    .managerRating(rating.getManagerRating())
                    .managerComment(rating.getManagerComment())
                    .build();
        }
    }

    public static ManagerReviewDTO fromEntity(ManagerReview review) {
        if (review == null) {
            return null;
        }

        SelfReviewSummaryDTO selfReviewSummary = null;
        if (review.getSelfReview() != null) {
            selfReviewSummary = SelfReviewSummaryDTO.builder()
                    .id(review.getSelfReview().getId())
                    .status(review.getSelfReview().getStatus())
                    .overallRating(review.getSelfReview().getOverallRating())
                    .build();
        }

        SelfReviewDTO.CycleSummaryDTO cycleSummary = null;
        if (review.getCycle() != null) {
            cycleSummary = SelfReviewDTO.CycleSummaryDTO.builder()
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

        return ManagerReviewDTO.builder()
                .id(review.getId())
                .selfReview(selfReviewSummary)
                .employee(EmployeeSummaryDTO.fromEntity(review.getEmployee()))
                .manager(EmployeeSummaryDTO.fromEntity(review.getManager()))
                .cycle(cycleSummary)
                .promotionRecommendation(review.getPromotionRecommendation())
                .trainingRecommendation(review.getTrainingRecommendation())
                .improvementPlan(review.getImprovementPlan())
                .overallRating(review.getOverallRating())
                .managerComments(review.getManagerComments())
                .status(review.getStatus())
                .completedOn(review.getCompletedOn())
                .goalRatings(goalRatings)
                .competencyRatings(compRatings)
                .build();
    }
}
