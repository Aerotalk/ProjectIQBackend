package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUpdateRequest {
    private String organizationName;
    private String organizationEmail;
    private String organizationPassword;
    private String legalName;
    private String organizationType;
    private String industry;
    private String status;
}
