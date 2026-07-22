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
public class ChallanLineItemDto {
    private UUID id;
    private String itemName;
    private String itemHsn;
    private String description;
    private BigDecimal dispatchedQuantity;
    private String unit;
}
