package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    @NotBlank(message = "Company Code is required")
    private String companyCode;

    @NotBlank(message = "Company Name is required")
    private String companyName;


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

    // Admin Login
    private String adminPassword; // Password for the auto-created company admin user

    // Branding
    private UUID logoFileId;
    private UUID invoiceLogoId;
    private UUID stampFileId;
    private String primaryColor;
    private String secondaryColor;
    private String termsAndConditions;
    
    @NotBlank(message = "Status is required")
    private String status;

    @Valid
    private List<CompanyAddressDto> addresses;
    
    @Valid
    private List<CompanyBankAccountDto> bankAccounts;
}
