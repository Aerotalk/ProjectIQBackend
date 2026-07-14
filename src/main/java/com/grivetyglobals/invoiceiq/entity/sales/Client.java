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
@Table(name = "sales_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sales_clients SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_no", length = 50)
    private String clientNo; // e.g., CUST-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "customer_type", length = 50)
    private String customerType;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "gst_treatment", length = 50)
    private String gstTreatment;

    @Column(length = 50)
    private String gstin;

    @Column(name = "pan_number", length = 50)
    private String panNumber;

    @Column(name = "place_of_supply", length = 100)
    private String placeOfSupply;

    @Column(name = "sez_unit_name", length = 255)
    private String sezUnitName;

    @Column(name = "lut_bond_no", length = 100)
    private String lutBondNo;

    @Column(length = 100)
    private String country;

    @Column(length = 10)
    private String currency;

    @Column(name = "foreign_tax_id", length = 100)
    private String foreignTaxId;

    @Column(name = "primary_contact_person", length = 255)
    private String primaryContactPerson;

    @Column(length = 100)
    private String designation;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "alternate_phone", length = 50)
    private String alternatePhone;

    @Column(name = "billing_address_line1", length = 255)
    private String billingAddressLine1;

    @Column(name = "billing_address_line2", length = 255)
    private String billingAddressLine2;

    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Column(name = "billing_state", length = 100)
    private String billingState;

    @Column(name = "billing_pin_code", length = 50)
    private String billingPinCode;

    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    @Column(name = "same_as_billing_address")
    private Boolean sameAsBillingAddress;

    @Column(name = "shipping_address_line1", length = 255)
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2", length = 255)
    private String shippingAddressLine2;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    @Column(name = "shipping_pin_code", length = 50)
    private String shippingPinCode;

    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

    @Column(length = 100)
    private String industry;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 50)
    private String status;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientAdditionalContact> additionalContacts;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
