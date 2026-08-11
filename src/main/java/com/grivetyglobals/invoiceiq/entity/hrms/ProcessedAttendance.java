package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_processed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProcessedAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private AttendancePeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "gross_working_days", precision = 5, scale = 2)
    private BigDecimal grossWorkingDays;

    @Column(name = "present_days", precision = 5, scale = 2)
    private BigDecimal presentDays;

    @Column(name = "absent_days", precision = 5, scale = 2)
    private BigDecimal absentDays;

    @Column(name = "leave_days", precision = 5, scale = 2)
    private BigDecimal leaveDays;

    @Column(name = "late_days")
    private Integer lateDays;

    @Column(name = "half_days")
    private Integer halfDays;

    @Column(name = "weekly_offs")
    private Integer weeklyOffs;

    @Column(name = "holidays")
    private Integer holidays;

    @Column(name = "lop_days", precision = 5, scale = 2)
    private BigDecimal lopDays;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "payable_days", precision = 5, scale = 2)
    private BigDecimal payableDays;

    @Column(name = "final_payable_days", precision = 5, scale = 2)
    private BigDecimal finalPayableDays;

    /** Draft, Finalized */
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "processed_by")
    private UUID processedBy;

    @Column(name = "processed_on")
    private LocalDateTime processedOn;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("employeeName")
    public String getEmployeeName() {
        if (employee != null) {
            String name = "";
            if (employee.getFirstName() != null) name += employee.getFirstName();
            if (employee.getLastName() != null) name += (name.isEmpty() ? "" : " ") + employee.getLastName();
            return name;
        }
        return null;
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("employeeCode")
    public String getEmployeeCode() {
        return employee != null ? employee.getEmployeeCode() : null;
    }
}
