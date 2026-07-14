package com.grivetyglobals.invoiceiq.entity.sales;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "sales_client_additional_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE sales_client_additional_contacts SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class ClientAdditionalContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 100)
    private String designation;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String phone;

    @Column(length = 50, nullable = false)
    private String role; // 'Billing' | 'Purchase' | 'Technical' | 'Other'

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}
