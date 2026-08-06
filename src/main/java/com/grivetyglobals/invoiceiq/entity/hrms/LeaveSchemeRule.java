package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_leave_scheme_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveSchemeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    private LeaveScheme scheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "annual_quota", precision = 5, scale = 2)
    private BigDecimal annualQuota;

    /** Monthly, Yearly, Half-Yearly */
    @Column(name = "accrual_frequency", length = 20)
    private String accrualFrequency;

    @Column(name = "carry_forward", precision = 5, scale = 2)
    private BigDecimal carryForward;

    @Column(name = "encashable")
    private Boolean encashable;

    @Column(name = "minimum_notice")
    private Integer minimumNotice;

    @Column(name = "maximum_consecutive_days")
    private Integer maximumConsecutiveDays;

    @Column(name = "medical_certificate_required")
    private Boolean medicalCertificateRequired;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
