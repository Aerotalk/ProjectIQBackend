package com.grivetyglobals.invoiceiq.entity.sales;

import com.grivetyglobals.invoiceiq.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "sales_quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sales_quotations SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quotation_no", length = 50, nullable = false)
    private String quotationNo; // e.g., QUOT-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "client_name", length = 255)
    private String clientName;

    @Column(name = "quotation_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(length = 255)
    private String subject;

    @Column(length = 100)
    private String reference;

    @Column(name = "sub_total", precision = 19, scale = 4)
    private BigDecimal subTotal;

    @Column(name = "total_discount", precision = 19, scale = 4)
    private BigDecimal totalDiscount;

    @Column(name = "delivery_cost", precision = 19, scale = 4)
    private BigDecimal deliveryCost;

    @Column(name = "total_taxable_amount", precision = 19, scale = 4)
    private BigDecimal totalTaxableAmount;

    @Column(name = "total_gst_amount", precision = 19, scale = 4)
    private BigDecimal totalGstAmount;

    @Column(name = "grand_total", precision = 19, scale = 4)
    private BigDecimal grandTotal;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(length = 50, nullable = false)
    private String status; // 'Draft' | 'Pending Approval' | 'Approved' | 'Sent to Client' | 'Confirmed Lead'

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "wo_po_document_url", length = 1024)
    private String woPoDocumentUrl;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationLineItem> lineItems;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
