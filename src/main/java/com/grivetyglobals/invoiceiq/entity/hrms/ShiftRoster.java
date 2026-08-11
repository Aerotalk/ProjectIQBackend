package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Employee;
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
@Table(name = "att_shift_rosters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShiftRoster {

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
    @JoinColumn(name = "assigned_shift_id", nullable = false)
    private Shift assignedShift;

    @Column(name = "roster_date", nullable = false)
    private LocalDate rosterDate;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "overridden")
    private Boolean overridden;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "published")
    private Boolean published;

    @Column(name = "published_by")
    private UUID publishedBy;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

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
    @com.fasterxml.jackson.annotation.JsonProperty("department")
    public String getDepartment() {
        return employee != null && employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null;
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("assignedShiftId")
    public UUID getAssignedShiftId() {
        return assignedShift != null ? assignedShift.getId() : null;
    }
}
