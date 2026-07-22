package com.grivetyglobals.invoiceiq.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderLineItemDto {
    private UUID id;
    private UUID productId;
    private String itemName;
    private String hsnSac;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal rate;
    private BigDecimal discount;
    private BigDecimal taxableAmount;
    private BigDecimal gstRate;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
}
