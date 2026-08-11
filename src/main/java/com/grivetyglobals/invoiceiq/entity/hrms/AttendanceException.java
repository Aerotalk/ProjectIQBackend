package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "att_exceptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceException {

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

    /** Missing Swipe, Late In, Early Out, Less Hours, Unauthorized Absence */
    @Column(name = "exception_type", length = 50, nullable = false)
    private String exceptionType;

    @Column(name = "description", length = 500)
    private String description;

    /** Low, Medium, High */
    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "resolved")
    private Boolean resolved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private Employee resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

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
