package com.grivetyglobals.invoiceiq.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiValidationError {
    private String field;
    private String message;
    private Object rejectedValue;
}
