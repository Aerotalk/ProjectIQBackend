package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entity representing Employee.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE employees SET deleted_at = CURRENT_TIMESTAMP WHERE employee_id = ?")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id", updatable = false, nullable = false)
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "employee_code", length = 30)
    private String employeeCode;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"organization", "company", "user", "department", "designation", "reportingManager", "hrManager"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    @Column(name = "profile_picture")
    private UUID profilePicture;

    @Column(name = "employment_status", length = 20)
    private String employmentStatus;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "employment_type", length = 50)
    private String employmentType;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "grade", length = 50)
    private String grade;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"organization", "company", "user", "department", "designation", "reportingManager", "hrManager"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_manager_id")
    private Employee hrManager;

    @Column(name = "weekly_off", length = 50)
    private String weeklyOff;

    @Column(name = "father_name", length = 100)
    private String fatherName;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
