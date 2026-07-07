package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCreateRequest {
    private String organizationCode;
    private String organizationName;
    private String organizationEmail;
    private String organizationPassword;
    private String legalName;
    private String organizationType;
    private String industry;
    private String status;
}
