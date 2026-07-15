package com.grivetyglobals.invoiceiq.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private UUID id; // Also returned as 'id' in frontend, maybe UUID string
    private String clientNo;
    private String customerType;
    private String companyName;
    private String firstName;
    private String lastName;
    private String displayName;
    private String gstTreatment;

    // GST & Tax fields
    private String gstin;
    private String panNumber;
    private String placeOfSupply;
    private String sezUnitName;
    private String lutBondNo;
    private String country;
    private String currency;
    private String foreignTaxId;

    // Contact Details
    private String primaryContactPerson;
    private String designation;
    private String email;
    private String phone;
    private String alternatePhone;
    private List<ClientAdditionalContactDto> additionalContacts;

    // Address Details
    private String billingAttention;
    private String billingPhone;
    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingCity;
    private String billingState;
    private String billingPinCode;
    private String billingCountry;

    private Boolean sameAsBillingAddress;
    private String shippingAttention;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPinCode;
    private String shippingCountry;

    // Commercial Settings
    private String paymentTerms;
    private BigDecimal creditLimit;
    private String industry;
    private String notes;
    private String status;
}
