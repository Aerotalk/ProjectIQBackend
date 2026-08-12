package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pay_payroll_runs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PayrollRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "payroll_period", length = 20, nullable = false)
    private String payrollPeriod;

    /** Regular, Supplementary, Arrears */
    @Column(name = "run_type", length = 20)
    private String runType;

    /** All Employees, Department, Selected Employees */
    @Column(name = "employee_scope", length = 30)
    private String employeeScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pay_payroll_run_employees",
        joinColumns = @JoinColumn(name = "payroll_run_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private java.util.List<com.grivetyglobals.invoiceiq.entity.Employee> selectedEmployees;

    /** Draft, Processing, Processed, Approved */
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "total_gross", precision = 14, scale = 2)
    private BigDecimal totalGross;

    @Column(name = "total_deductions", precision = 14, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "total_net", precision = 14, scale = 2)
    private BigDecimal totalNet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "processed_on")
    private LocalDateTime processedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_on")
    private LocalDateTime approvedOn;

    /** Unpaid, Pending, Paid */
    @Column(name = "payout_status", length = 20)
    private String payoutStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
