package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCreateRequest {
    private UUID organizationId;
    
    private String companyCode;
    private String companyName;
    private String legalName;
    private String gstNumber;
    private String panNumber;
    private String tanNumber;
    private String cinNumber;
    private String msmeNumber;
    private String iecCode;
    private String email;
    private String phone;
    private String website;
    private String primaryColor;
    private String secondaryColor;
    private String status;

    private List<CompanyAddressDto> addresses;
    private List<CompanyBankAccountDto> bankAccounts;
}
