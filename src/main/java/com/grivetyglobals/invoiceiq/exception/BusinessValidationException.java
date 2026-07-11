package com.grivetyglobals.invoiceiq.exception;

import org.springframework.http.HttpStatus;

public class BusinessValidationException extends AppException {
    public BusinessValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
