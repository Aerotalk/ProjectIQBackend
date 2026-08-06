package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "shift_name", length = 100, nullable = false)
    private String shiftName;

    @Column(name = "shift_code", length = 30)
    private String shiftCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "grace_time")
    private Integer graceTime;

    @Column(name = "late_grace_minutes")
    private Integer lateGraceMinutes;

    @Column(name = "early_exit_grace_minutes")
    private Integer earlyExitGraceMinutes;

    @Column(name = "half_day_hours", precision = 4, scale = 2)
    private BigDecimal halfDayHours;

    @Column(name = "full_day_hours", precision = 4, scale = 2)
    private BigDecimal fullDayHours;

    @Column(name = "weekly_hours", precision = 4, scale = 2)
    private BigDecimal weeklyHours;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    @Column(name = "flexible_shift")
    private Boolean flexibleShift;

    @Column(name = "overtime_allowed")
    private Boolean overtimeAllowed;

    @Column(name = "night_shift")
    private Boolean nightShift;

    @Column(name = "active")
    private Boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
