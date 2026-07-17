package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private java.util.List<String> roles;
    private java.util.UUID organizationId;
    private String organizationName;
    private java.util.UUID companyId;
    private String companyName;
    private java.util.Set<String> effectivePermissions;
    private java.util.UUID profilePhotoId;
    private java.util.UUID companyLogoId;
}
