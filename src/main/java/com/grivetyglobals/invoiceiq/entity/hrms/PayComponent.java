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
@Table(name = "pay_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PayComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "component_name", length = 100, nullable = false)
    private String componentName;

    @Column(name = "code", length = 30)
    private String code;

    /** Earning, Deduction, Reimbursement */
    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "sub_type", length = 50)
    private String subType;

    /** Flat Amount, Percentage, Formula */
    @Column(name = "calculation_type", length = 30)
    private String calculationType;

    @Column(name = "percentage_of", length = 50)
    private String percentageOf;

    @Column(name = "percentage_value", precision = 5, scale = 2)
    private BigDecimal percentageValue;

    @Column(name = "max_limit", precision = 12, scale = 2)
    private BigDecimal maxLimit;

    @Column(name = "taxable")
    private Boolean taxable;

    @Column(name = "pro_rata")
    private Boolean proRata;

    @Column(name = "part_of_ctc")
    private Boolean partOfCTC;

    @Column(name = "part_of_gross")
    private Boolean partOfGross;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "active")
    private Boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
