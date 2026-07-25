package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;

/**
 * Data Transfer Object for RefreshTokenRequest.
 */
@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
