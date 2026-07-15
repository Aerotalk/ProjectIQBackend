package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE companies SET deleted_at = CURRENT_TIMESTAMP WHERE company_id=?")
@SQLRestriction("deleted_at IS NULL")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "company_code", length = 20, unique = true)
    private String companyCode;

    @Column(name = "company_name", length = 255, nullable = false)
    private String companyName;

    @Column(name = "legal_name", length = 255)
    private String legalName;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "tan_number", length = 20)
    private String tanNumber;

    @Column(name = "cin_number", length = 30)
    private String cinNumber;

    @Column(name = "msme_number", length = 30)
    private String msmeNumber;

    @Column(name = "iec_code", length = 30)
    private String iecCode;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(name = "logo_file_id")
    private UUID logoFileId;

    @Column(name = "invoice_logo_id")
    private UUID invoiceLogoId;

    @Column(name = "stamp_file_id")
    private UUID stampFileId;

    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    @Column(name = "secondary_color", length = 20)
    private String secondaryColor;

    @Column(length = 20)
    private String status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @org.hibernate.annotations.BatchSize(size = 20)
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<CompanyAddress> addresses;

    @org.hibernate.annotations.BatchSize(size = 20)
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<CompanyBankAccount> bankAccounts;

    @org.hibernate.annotations.BatchSize(size = 20)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.Set<CompanyApplication> companyApplications = new java.util.HashSet<>();
}
