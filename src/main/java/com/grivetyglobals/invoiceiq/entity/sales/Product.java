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

@Entity
@Table(name = "sales_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sales_products SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "item_code", length = 50)
    private String itemCode; // e.g., PROD-0001 or ITEM-001

    @Column(name = "item_name", length = 255, nullable = false)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50, nullable = false)
    private String type; // 'Product' | 'Service'

    @Column(length = 50)
    private String unit;

    @Column(name = "standard_rate", precision = 19, scale = 4)
    private BigDecimal standardRate;

    @Column(name = "hsn_sac", length = 50)
    private String hsnSac;

    @Column(name = "gst_rate", length = 20)
    private String gstRate;

    @Column(length = 50)
    private String status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
