package com.grivetyglobals.invoiceiq.entity.hrms.performance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "prf_manager_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ManagerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_review_id")
    private SelfReview selfReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private AppraisalCycle cycle;

    @Column(name = "promotion_recommendation", length = 100)
    private String promotionRecommendation;

    @Column(name = "training_recommendation", length = 500)
    private String trainingRecommendation;

    @Column(name = "improvement_plan", length = 2000)
    private String improvementPlan;

    @Column(name = "overall_rating", precision = 4, scale = 2)
    private BigDecimal overallRating;

    @Column(name = "manager_comments", length = 2000)
    private String managerComments;

    /** Pending, Completed */
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "completed_on")
    private LocalDateTime completedOn;

    @OneToMany(mappedBy = "managerReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ManagerReviewGoalRating> goalRatings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "managerReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ManagerReviewCompetencyRating> competencyRatings = new LinkedHashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
