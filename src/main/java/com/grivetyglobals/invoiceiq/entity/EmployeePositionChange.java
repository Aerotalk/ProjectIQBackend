package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an employee position or role change event.
 * Tracks promotions, demotions, transfers, and reporting line changes.
 */
@Entity
@Table(name = "employee_position_changes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeePositionChange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "change_type", length = 50)
    private String changeType;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "department_id", length = 100)
    private String departmentId;

    @Column(name = "designation_id", length = 100)
    private String designationId;

    @Column(name = "grade", length = 50)
    private String grade;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "reporting_manager_id")
    private UUID reportingManagerId;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
