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
@Table(name = "att_leave_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "opening_balance", precision = 5, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "granted", precision = 5, scale = 2)
    private BigDecimal granted;

    @Column(name = "availed", precision = 5, scale = 2)
    private BigDecimal availed;

    @Column(name = "encashed", precision = 5, scale = 2)
    private BigDecimal encashed;

    @Column(name = "lapsed", precision = 5, scale = 2)
    private BigDecimal lapsed;

    @Column(name = "available", precision = 5, scale = 2)
    private BigDecimal available;

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
    @com.fasterxml.jackson.annotation.JsonProperty("leaveTypeName")
    public String getLeaveTypeName() {
        return leaveType != null ? leaveType.getName() : null;
    }
}
