package com.grivetyglobals.invoiceiq.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private BigDecimal amount;
    private String remarks;
    private String status;
    private UUID attachmentFileId;
}
