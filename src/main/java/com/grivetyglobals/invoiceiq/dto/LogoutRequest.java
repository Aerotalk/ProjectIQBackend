package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
