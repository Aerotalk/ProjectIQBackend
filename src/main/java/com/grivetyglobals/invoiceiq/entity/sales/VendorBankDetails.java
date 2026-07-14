package com.grivetyglobals.invoiceiq.entity.sales;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "sales_vendor_bank_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE sales_vendor_bank_details SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class VendorBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "account_name", length = 255, nullable = false)
    private String accountName;

    @Column(name = "account_number", length = 100, nullable = false)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 50, nullable = false)
    private String ifscCode;

    @Column(name = "bank_name", length = 255, nullable = false)
    private String bankName;

    @Column(name = "branch_name", length = 255)
    private String branchName;

    @Column(name = "swift_code", length = 50)
    private String swiftCode;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}
