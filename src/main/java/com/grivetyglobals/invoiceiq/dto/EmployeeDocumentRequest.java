package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for a single employee document entry.
 * The fileId is a UUID obtained after uploading the file via POST /api/files.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDocumentRequest {

    private String documentCategory;
    private String documentName;
    /** UUID from the files table after file upload — null if no file uploaded yet */
    private UUID fileId;
    private String expiryDate;
}
