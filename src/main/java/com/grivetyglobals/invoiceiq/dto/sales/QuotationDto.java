package com.grivetyglobals.invoiceiq.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDto {
    private UUID id;
    private String quotationNo;
    private UUID clientId;
    private String clientName;
    private LocalDateTime date;
    private LocalDateTime validUntil;
    private String subject;
    private String reference;
    private String salesperson;
    private String templateName;

    private List<QuotationLineItemDto> lineItems;

    // Totals
    private BigDecimal subTotal;
    private BigDecimal totalDiscount;
    private BigDecimal deliveryCost;
    private BigDecimal totalTaxableAmount;
    private BigDecimal totalGstAmount;
    private BigDecimal grandTotal;

    private String notes;
    private String termsAndConditions;

    private String status;

    // Workflow specifics
    private String approvedBy;
    private LocalDateTime approvalDate;
    private String woPoDocumentUrl;
    
    private LocalDateTime createdAt;
}
