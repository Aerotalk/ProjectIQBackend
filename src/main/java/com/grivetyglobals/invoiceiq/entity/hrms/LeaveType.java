package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 20)
    private String code;

    /** Paid, Unpaid, Comp Off */
    @Column(name = "category", length = 20)
    private String category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "requires_approval")
    private Boolean requiresApproval;

    @Column(name = "requires_attachment")
    private Boolean requiresAttachment;

    @Column(name = "minimum_days", precision = 4, scale = 2)
    private BigDecimal minimumDays;

    @Column(name = "maximum_days", precision = 4, scale = 2)
    private BigDecimal maximumDays;

    @Column(name = "gender_restriction", length = 20)
    private String genderRestriction;

    @Column(name = "probation_allowed")
    private Boolean probationAllowed;

    @Column(name = "notice_period_required")
    private Integer noticePeriodRequired;

    @Column(name = "allow_half_day")
    private Boolean allowHalfDay;

    @Column(name = "allow_hourly_leave")
    private Boolean allowHourlyLeave;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
