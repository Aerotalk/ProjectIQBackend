package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;

/**
 * Data Transfer Object for LogoutRequest.
 */
@Data
public class LogoutRequest {
    private String refreshToken;
}
