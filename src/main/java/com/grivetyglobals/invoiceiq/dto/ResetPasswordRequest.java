package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;

/**
 * Data Transfer Object for ResetPasswordRequest.
 */
@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
