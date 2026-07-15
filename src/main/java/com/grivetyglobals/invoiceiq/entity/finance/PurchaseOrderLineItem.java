package com.grivetyglobals.invoiceiq.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_line_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PurchaseOrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(length = 50)
    private String unit;

    @Column(precision = 15, scale = 4)
    private BigDecimal rate;

    @Column(precision = 15, scale = 4)
    private BigDecimal discount;

    @Column(name = "taxable_amount", precision = 15, scale = 4)
    private BigDecimal taxableAmount;

    @Column(name = "gst_rate", precision = 5, scale = 2)
    private BigDecimal gstRate;

    @Column(name = "gst_amount", precision = 15, scale = 4)
    private BigDecimal gstAmount;

    @Column(name = "total_amount", precision = 15, scale = 4)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
