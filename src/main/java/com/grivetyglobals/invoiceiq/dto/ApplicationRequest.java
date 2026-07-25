package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for ApplicationRequest.
 */
@Data
public class ApplicationRequest {

    @NotBlank(message = "Application Code is required")
    private String applicationCode;

    @NotBlank(message = "Application Name is required")
    private String applicationName;

    private String applicationRoute;
    
    private String icon;
    
    private String description;
    
    private String status;
}
