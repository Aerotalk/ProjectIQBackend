package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a salary revision event for an employee.
 * Each revision is an append-only historical record.
 */
@Entity
@Table(name = "employee_salary_revisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeeSalaryRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "revision_type", length = 50)
    private String revisionType;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "annual_ctc", precision = 15, scale = 2)
    private BigDecimal annualCTC;

    @Column(name = "increment_percentage", precision = 5, scale = 2)
    private BigDecimal incrementPercentage;

    @Column(name = "salary_components", length = 1000)
    private String salaryComponents;

    @Column(name = "reason", length = 500)
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
