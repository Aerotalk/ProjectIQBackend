package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateRequest {
    private String username;
    private String email;
    private String mobile;
    private String status;
    private String password;
    private String role;
    private UUID organizationId;
    private UUID companyId;
    private java.util.List<UUID> roleIds;
}
