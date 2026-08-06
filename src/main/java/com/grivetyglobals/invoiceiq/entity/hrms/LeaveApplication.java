package com.grivetyglobals.invoiceiq.entity.hrms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "att_leave_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LeaveApplication {

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
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "leave_number", length = 30)
    private String leaveNumber;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    /** Session 1 or Session 2 */
    @Column(name = "from_session", length = 20)
    private String fromSession;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    /** Session 1 or Session 2 */
    @Column(name = "to_session", length = 20)
    private String toSession;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "attachment", length = 500)
    private String attachment;

    @Column(name = "duration", precision = 5, scale = 2)
    private BigDecimal duration;

    @Column(name = "applied_on")
    private LocalDate appliedOn;

    /** Pending, Approved, Rejected, Cancelled */
    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employee approver;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_remarks", length = 500)
    private String approvalRemarks;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
