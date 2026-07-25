package com.grivetyglobals.invoiceiq.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Data Transfer Object for UpdateProfileRequest.
 */
@Data
public class UpdateProfileRequest {
    private String username;
    private UUID profilePhotoId;
}
