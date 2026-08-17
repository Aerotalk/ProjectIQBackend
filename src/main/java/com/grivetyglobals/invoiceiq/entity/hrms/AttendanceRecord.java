package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @JsonIgnoreProperties({"user", "reportingManager", "hrManager", "organization", "company"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @JsonIgnoreProperties({"organization", "company"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "working_hours", precision = 4, scale = 2)
    private BigDecimal workingHours;

    @Column(name = "break_hours", precision = 4, scale = 2)
    private BigDecimal breakHours;

    @Column(name = "overtime_hours", precision = 4, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "late_by")
    private Integer lateBy;

    @Column(name = "early_exit")
    private Integer earlyExit;

    /** Present, Absent, Leave, Holiday, Weekend, Half Day */
    @Column(name = "status", length = 30)
    private String status;

    /** Biometric, Mobile, Manual, Web, Import */
    @Column(name = "attendance_source", length = 30)
    private String attendanceSource;

    @Column(name = "regularization_status", length = 20)
    private String regularizationStatus;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus;

    @Column(name = "exception_type", length = 50)
    private String exceptionType;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "regularized")
    private Boolean regularized;

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

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("departmentId")
    public String getDepartmentId() {
        return employee != null && employee.getDepartment() != null ? employee.getDepartment().getId().toString() : null;
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("designationId")
    public String getDesignationId() {
        return employee != null && employee.getDesignation() != null ? employee.getDesignation().getId().toString() : null;
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("locationId")
    public String getLocationId() {
        return employee != null ? employee.getLocation() : null;
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("shiftName")
    public String getShiftName() {
        return shift != null ? shift.getShiftName() : null;
    }
}
