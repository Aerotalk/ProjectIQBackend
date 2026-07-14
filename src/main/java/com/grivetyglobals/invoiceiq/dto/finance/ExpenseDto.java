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
    private String expenseType;
    private LocalDate expenseDate;
    private BigDecimal amount;
    private String remarks;
    private String status;
    private UUID attachmentFileId;
}
