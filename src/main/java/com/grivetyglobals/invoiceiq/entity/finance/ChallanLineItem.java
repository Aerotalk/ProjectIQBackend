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
@Table(name = "challan_line_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChallanLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challan_id", nullable = false)
    private Challan challan;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "hsn_sac", length = 50)
    private String hsnSac;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(name = "item_hsn", length = 50)
    private String itemHsn;

    @Column(name = "dispatched_quantity", precision = 10, scale = 2)
    private BigDecimal dispatchedQuantity;

    @Column(length = 50)
    private String unit;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
