package com.grivetyglobals.invoiceiq.entity.hrms.performance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prf_appraisal_cycles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppraisalCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    /** Annual, Bi-Annual, Quarterly */
    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "period", length = 100)
    private String period;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "self_review_deadline")
    private LocalDate selfReviewDeadline;

    @Column(name = "manager_review_deadline")
    private LocalDate managerReviewDeadline;

    @Column(name = "hr_review_deadline")
    private LocalDate hrReviewDeadline;

    /** Draft, Active, Review Phase, Completed */
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "eligible_count")
    private Integer eligibleCount;

    @Column(name = "completion_percentage")
    private Integer completionPercentage;

    @Column(name = "description", length = 500)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
