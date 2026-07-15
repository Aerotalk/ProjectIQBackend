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
public class ExpenseDto {
    private UUID id;
    private UUID projectId;
    private String projectName;
    private String category;
    private LocalDate expenseDate;
    private BigDecimal amount;
    private String description;
    private String paidBy;
    private Boolean isGstApplicable;
    private BigDecimal gstAmount;
    private Boolean isInputCreditClaimable;
    private String remarks;
    private String status;
    private UUID attachmentFileId;
    private String receiptName;
}
