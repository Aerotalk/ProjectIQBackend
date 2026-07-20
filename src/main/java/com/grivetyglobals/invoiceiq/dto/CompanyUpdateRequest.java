package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CompanyUpdateRequest {
    @NotBlank(message = "Company Name is required")
    private String companyName;

    @NotBlank(message = "Legal Name is required")
    private String legalName;

    // Tax Information
    private String gstNumber;
    private String panNumber;
    private String tanNumber;
    private String cinNumber;
    private String msmeNumber;
    private String iecCode;

    // Contact Information
    @Email(message = "Invalid email format")
    private String email;
    private String phone;
    private String website;

    // Branding
    private UUID logoFileId;
    private UUID invoiceLogoId;
    private UUID stampFileId;
    private String primaryColor;
    private String secondaryColor;
    private String termsAndConditions;
    
    // Admin Account
    private String adminPassword;

    @Valid
    private List<CompanyAddressDto> addresses;
    
    @Valid
    private List<CompanyBankAccountDto> bankAccounts;
}
