package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    private String name;
    private String email;
    private String password;
    private UUID organizationId;
    private String role; // e.g. "ROLE_ORGANIZATION_ADMIN"
}
