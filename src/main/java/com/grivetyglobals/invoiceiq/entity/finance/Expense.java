package com.grivetyglobals.invoiceiq.entity.finance;

import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(length = 100)
    private String category;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "paid_by", length = 255)
    private String paidBy;

    @Column(name = "is_gst_applicable")
    private Boolean isGstApplicable;

    @Column(name = "gst_amount", precision = 15, scale = 2)
    private BigDecimal gstAmount;

    @Column(name = "is_input_credit_claimable")
    private Boolean isInputCreditClaimable;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(length = 50)
    private String status;

    @Column(name = "attachment_file_id")
    private UUID attachmentFileId;

    @Column(name = "receipt_name", length = 255)
    private String receiptName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
