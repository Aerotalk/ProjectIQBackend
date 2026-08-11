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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ecl_reviewer_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReviewerAssignment {

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
    @JoinColumn(name = "template_id")
    private ExpenseClaimTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer1_id")
    private Employee reviewer1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer2_id")
    private Employee reviewer2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer3_id")
    private Employee reviewer3;

    @Column(name = "auto_escalation_days")
    private Integer autoEscalationDays;

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

    @Transient
    @JsonProperty("templateName")
    public String getFlatTemplateName() {
        return template != null ? template.getTemplateName() : null;
    }

    @Transient
    @JsonProperty("reviewer1Name")
    public String getFlatReviewer1Name() {
        return reviewer1 != null ? reviewer1.getFirstName() + " " + reviewer1.getLastName() : null;
    }

    @Transient
    @JsonProperty("reviewer2Name")
    public String getFlatReviewer2Name() {
        return reviewer2 != null ? reviewer2.getFirstName() + " " + reviewer2.getLastName() : null;
    }

    @Transient
    @JsonProperty("reviewer3Name")
    public String getFlatReviewer3Name() {
        return reviewer3 != null ? reviewer3.getFirstName() + " " + reviewer3.getLastName() : null;
    }
}
