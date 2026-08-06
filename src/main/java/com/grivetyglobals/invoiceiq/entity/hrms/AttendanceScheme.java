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
@Table(name = "att_schemes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "scheme_name", length = 100, nullable = false)
    private String schemeName;

    @Column(name = "scheme_description", length = 500)
    private String schemeDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_shift_id")
    private Shift defaultShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_list_id")
    private HolidayList holidayList;

    /** JSON string representing weekend config e.g. {"M":false,"T":false,"S":true,...} */
    @Column(name = "weekend_configuration", columnDefinition = "TEXT")
    private String weekendConfiguration;

    @Column(name = "require_live_validation")
    private Boolean requireLiveValidation;

    @Column(name = "late_policy", length = 50)
    private String latePolicy;

    @Column(name = "overtime_policy", length = 50)
    private String overtimePolicy;

    @Column(name = "minimum_hours", precision = 4, scale = 2)
    private BigDecimal minimumHours;

    @Column(name = "half_day_hours", precision = 4, scale = 2)
    private BigDecimal halfDayHours;

    @Column(name = "grace_minutes")
    private Integer graceMinutes;

    @Column(name = "auto_regularization")
    private Boolean autoRegularization;

    @Column(name = "allow_mobile_attendance")
    private Boolean allowMobileAttendance;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
