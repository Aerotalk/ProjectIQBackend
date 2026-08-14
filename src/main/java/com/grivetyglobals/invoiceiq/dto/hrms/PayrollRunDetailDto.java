package com.grivetyglobals.invoiceiq.dto.hrms;

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
public class PayrollRunDetailDto {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private BigDecimal basicPay;
    private BigDecimal hra;
    private BigDecimal specialAllowance;
    private BigDecimal gross;
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;
    private BigDecimal tdsDeduction;
    private BigDecimal professionalTax;
    private BigDecimal totalDeductions;
    private BigDecimal net;
    private BigDecimal lopDays;
    private BigDecimal payableDays;
    private java.util.List<PayrollRunDetailComponentDto> components;
}
