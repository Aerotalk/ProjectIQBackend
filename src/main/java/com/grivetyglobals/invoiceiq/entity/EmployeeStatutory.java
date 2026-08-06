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
 * Entity representing an employee's statutory/compliance details.
 * Includes PAN, Aadhaar, UAN, PF, ESI, Passport, Voter ID, Driving License.
 */
@Entity
@Table(name = "employee_statutory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeeStatutory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "aadhaar_number", length = 20)
    private String aadhaarNumber;

    @Column(name = "uan", length = 30)
    private String uan;

    @Column(name = "pf_number", length = 30)
    private String pfNumber;

    @Column(name = "esi_number", length = 30)
    private String esiNumber;

    @Column(name = "passport_number", length = 30)
    private String passportNumber;

    @Column(name = "passport_expiry")
    private LocalDate passportExpiry;

    @Column(name = "voter_id", length = 30)
    private String voterId;

    @Column(name = "driving_license", length = 30)
    private String drivingLicense;

    @Column(name = "driving_license_expiry")
    private LocalDate drivingLicenseExpiry;

    @Column(name = "pf_applicable")
    private Boolean pfApplicable;

    @Column(name = "esi_applicable")
    private Boolean esiApplicable;

    @Column(name = "tax_regime", length = 20)
    private String taxRegime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
