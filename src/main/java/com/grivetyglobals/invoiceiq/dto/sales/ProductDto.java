package com.grivetyglobals.invoiceiq.dto.sales;

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
public class ProductDto {
    private UUID id;
    private String itemCode;
    private String itemName;
    private String description;
    private String type; // Product or Service
    private String unit;
    private BigDecimal standardRate;
    private String hsnSac;
    private String gstRate;
    private String status;
}
