package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pay_payroll_run_detail_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PayrollRunDetailComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_detail_id", nullable = false)
    private PayrollRunDetail payrollRunDetail;

    @Column(name = "component_name", length = 100, nullable = false)
    private String componentName;

    // "EARNING" | "DEDUCTION" | "REIMBURSEMENT"
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "display_order")
    private Integer displayOrder;
}
