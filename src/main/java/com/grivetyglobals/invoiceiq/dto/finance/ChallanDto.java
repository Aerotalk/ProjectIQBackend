package com.grivetyglobals.invoiceiq.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallanDto {
    private UUID id;
    private UUID vendorId;
    private String vendorName;
    private UUID projectId;
    private String projectName;
    private String challanNumber;
    private String ewayBillNo;
    private LocalDate challanDate;
    private String description;
    private UUID linkedVendorPoId;
    private String linkedVendorPoNumber;
    private String remarks;
    private String status;
    private UUID attachmentFileId;
    private String attachmentName;
    private String transportMode;
    private String deliveryLocation;
    private String placeOfSupply;
    private String contactName;
    private String contactEmail;
    private String contactMobile;
    private String poNumber;
    private LocalDate poDate;
    private List<ChallanLineItemDto> lineItems;
}
