package com.grivetyglobals.invoiceiq.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class MeResponse {
    private UUID id;
    private String username;
    private String email;
    private List<String> roles;
    private UUID organizationId;
    private String organizationName;
    private UUID companyId;
    private String companyName;
    private Set<String> effectivePermissions;
    private UUID profilePhotoId;
}
