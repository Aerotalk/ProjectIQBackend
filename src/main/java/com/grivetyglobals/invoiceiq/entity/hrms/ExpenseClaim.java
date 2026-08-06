package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ecl_claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExpenseClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "claim_no", length = 30, nullable = false)
    private String claimNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ExpenseClaimTemplate template;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "total_claimed", precision = 12, scale = 2)
    private BigDecimal totalClaimed;

    @Column(name = "approved_amount", precision = 12, scale = 2)
    private BigDecimal approvedAmount;

    /** Draft, Submitted, Level 1 Approved, Level 2 Approved, Level 3 Approved, Approved, Rejected, Sent Back, Paid */
    @Column(name = "status", length = 30)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_reviewer_id")
    private Employee currentReviewer;

    @Column(name = "submitted_on")
    private LocalDateTime submittedOn;

    @Column(name = "outstanding_advance", precision = 12, scale = 2)
    private BigDecimal outstandingAdvance;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
