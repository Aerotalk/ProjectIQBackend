package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "ecl_advances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExpenseAdvance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "advance_no", length = 30, nullable = false)
    private String advanceNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "trip_or_project", length = 200)
    private String tripOrProject;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    /** Pending, Approved, Disbursed, Settled, Rejected */
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "disbursed")
    private Boolean disbursed;

    @Column(name = "outstanding_balance", precision = 12, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "purpose", length = 500)
    private String purpose;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    @JsonProperty("employeeName")
    public String getFlatEmployeeName() {
        return employee != null ? employee.getFirstName() + " " + employee.getLastName() : null;
    }
}
