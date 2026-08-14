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
@Table(name = "pay_payroll_run_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PayrollRunDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "gross", precision = 12, scale = 2)
    private BigDecimal gross;

    @Column(name = "total_deductions", precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "net", precision = 12, scale = 2)
    private BigDecimal net;

    @Column(name = "lop_days", precision = 5, scale = 2)
    private BigDecimal lopDays;

    @Column(name = "payable_days", precision = 5, scale = 2)
    private BigDecimal payableDays;

    @OneToMany(mappedBy = "payrollRunDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PayrollRunDetailComponent> components;

    @Deprecated
    @Column(name = "basic_pay", precision = 12, scale = 2)
    private BigDecimal basicPay;

    @Deprecated
    @Column(name = "hra", precision = 12, scale = 2)
    private BigDecimal hra;

    @Deprecated
    @Column(name = "special_allowance", precision = 12, scale = 2)
    private BigDecimal specialAllowance;

    @Column(name = "pf_deduction", precision = 12, scale = 2)
    private BigDecimal pfDeduction;

    @Column(name = "esi_deduction", precision = 12, scale = 2)
    private BigDecimal esiDeduction;

    @Column(name = "tds_deduction", precision = 12, scale = 2)
    private BigDecimal tdsDeduction;

    @Column(name = "professional_tax", precision = 12, scale = 2)
    private BigDecimal professionalTax;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
