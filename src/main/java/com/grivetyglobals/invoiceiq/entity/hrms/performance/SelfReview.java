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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prf_self_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SelfReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private AppraisalCycle cycle;

    @Column(name = "strengths", length = 2000)
    private String strengths;

    @Column(name = "areas_of_improvement", length = 2000)
    private String areasOfImprovement;

    @Column(name = "overall_rating", precision = 4, scale = 2)
    private BigDecimal overallRating;

    /** Draft, Submitted */
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "submitted_on")
    private LocalDateTime submittedOn;

    @OneToMany(mappedBy = "selfReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SelfReviewGoalRating> goalRatings = new ArrayList<>();

    @OneToMany(mappedBy = "selfReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SelfReviewCompetencyRating> competencyRatings = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
