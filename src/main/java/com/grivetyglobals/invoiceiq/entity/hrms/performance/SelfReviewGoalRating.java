package com.grivetyglobals.invoiceiq.entity.hrms.performance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "prf_self_review_goal_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SelfReviewGoalRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_review_id", nullable = false)
    private SelfReview selfReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private PerformanceGoal goal;

    @Column(name = "employee_rating", precision = 4, scale = 2)
    private BigDecimal employeeRating;

    @Column(name = "employee_comment", length = 1000)
    private String employeeComment;
}
