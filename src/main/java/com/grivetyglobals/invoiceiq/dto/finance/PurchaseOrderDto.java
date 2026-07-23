package com.grivetyglobals.invoiceiq.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDto {
    private UUID id;
    private UUID vendorId;
    private String vendorName;
    private UUID projectId;
    private String projectName;
    private String poNumber;
    private LocalDate poDate;
    private BigDecimal grandTotal;
    private String description;
    private String internalNotes;
    private LocalDate expectedDelivery;
    private String status;
    private UUID attachmentFileId;
    private String attachmentName;
    private String templateName;
    private java.time.LocalDateTime createdAt;
    private List<PurchaseOrderLineItemDto> lineItems;
}
