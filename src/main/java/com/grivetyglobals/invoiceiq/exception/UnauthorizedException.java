package com.grivetyglobals.invoiceiq.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for UnauthorizedException.
 */
public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
