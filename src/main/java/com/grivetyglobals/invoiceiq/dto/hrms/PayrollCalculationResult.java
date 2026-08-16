package com.grivetyglobals.invoiceiq.dto.hrms;

import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetailComponent;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PayrollCalculationResult {
    private BigDecimal basicPay;
    private BigDecimal hra;
    private BigDecimal specialAllowance;
    
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;
    private BigDecimal tdsDeduction;
    private BigDecimal professionalTax;
    
    private BigDecimal gross;
    private BigDecimal totalDeductions;
    private BigDecimal net;
    
    private BigDecimal totalLopDays;
    private BigDecimal payableDays;

    private List<PayrollRunDetailComponent> detailComponents;
}
