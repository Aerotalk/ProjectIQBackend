package com.grivetyglobals.invoiceiq.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for BusinessValidationException.
 */
public class BusinessValidationException extends AppException {
    public BusinessValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
