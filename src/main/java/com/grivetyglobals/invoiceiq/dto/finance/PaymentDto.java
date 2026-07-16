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
public class PaymentDto {
    private UUID id;
    private String paymentId;
    private UUID projectId;
    private String projectName;
    private String paymentDate;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String referenceId;
    private String notes;
    private String status;
    private String createdAt;
    private String updatedAt;
}
