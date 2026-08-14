package com.grivetyglobals.invoiceiq.service.hrms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grivetyglobals.invoiceiq.dto.SalaryComponentDto;
import com.grivetyglobals.invoiceiq.dto.hrms.PayrollCalculationInput;
import com.grivetyglobals.invoiceiq.dto.hrms.PayrollCalculationResult;
import com.grivetyglobals.invoiceiq.entity.hrms.EmployeeLOP;
import com.grivetyglobals.invoiceiq.entity.hrms.PayrollRunDetailComponent;
import com.grivetyglobals.invoiceiq.entity.hrms.ReimbursementClaim;
import com.grivetyglobals.invoiceiq.entity.hrms.SalaryInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PayrollCalculationEngine {

    private final ObjectMapper objectMapper;

    public PayrollCalculationResult calculate(PayrollCalculationInput input) {
        BigDecimal annualCTC = input.getSalaryRevision().getAnnualCTC();
        BigDecimal monthlyGross = BigDecimal.ZERO;
        BigDecimal basicPay = BigDecimal.ZERO;
        BigDecimal hra = BigDecimal.ZERO;
        BigDecimal specialAllowance = BigDecimal.ZERO;

        List<SalaryComponentDto> dtos = null;
        if (input.getSalaryRevision().getSalaryComponents() != null && !input.getSalaryRevision().getSalaryComponents().isBlank()) {
            try {
                dtos = objectMapper.readValue(
                        input.getSalaryRevision().getSalaryComponents(),
                        new TypeReference<List<SalaryComponentDto>>() {}
                );
            } catch (Exception ignored) {}
        }

        List<PayrollRunDetailComponent> detailComponents = new ArrayList<>();
        BigDecimal computedEarnings = BigDecimal.ZERO;
        BigDecimal computedDeductions = BigDecimal.ZERO;

        if (dtos != null && !dtos.isEmpty()) {
            for (int i = 0; i < dtos.size(); i++) {
                SalaryComponentDto dto = dtos.get(i);
                BigDecimal amount = BigDecimal.ZERO;
                if (dto.getPercentage() != null) {
                    amount = annualCTC.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                            .multiply(dto.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else if (dto.getAmount() != null) {
                    amount = dto.getAmount().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                }

                if ("EARNING".equalsIgnoreCase(dto.getType())) {
                    computedEarnings = computedEarnings.add(amount);
                } else if ("DEDUCTION".equalsIgnoreCase(dto.getType())) {
                    computedDeductions = computedDeductions.add(amount);
                }

                if ("Basic".equalsIgnoreCase(dto.getComponentName()) || "Basic Pay".equalsIgnoreCase(dto.getComponentName())) {
                    basicPay = amount;
                } else if ("HRA".equalsIgnoreCase(dto.getComponentName())) {
                    hra = amount;
                }

                PayrollRunDetailComponent comp = new PayrollRunDetailComponent();
                comp.setComponentName(dto.getComponentName());
                comp.setType(dto.getType() != null ? dto.getType() : "EARNING");
                comp.setAmount(amount);
                comp.setDisplayOrder(i);
                detailComponents.add(comp);
            }
            monthlyGross = computedEarnings;
            specialAllowance = monthlyGross.subtract(basicPay).subtract(hra);
            if (basicPay.compareTo(BigDecimal.ZERO) == 0 && monthlyGross.compareTo(BigDecimal.ZERO) > 0) {
                basicPay = monthlyGross; // fallback for statutory calc if no basic found
            }
        } else {
            // Fallback to legacy ratio
            monthlyGross = annualCTC.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            basicPay = monthlyGross.multiply(BigDecimal.valueOf(0.40)).setScale(2, RoundingMode.HALF_UP);
            hra = basicPay.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
            specialAllowance = monthlyGross.subtract(basicPay).subtract(hra);

            detailComponents.add(PayrollRunDetailComponent.builder().componentName("Basic").type("EARNING").amount(basicPay).displayOrder(1).build());
            detailComponents.add(PayrollRunDetailComponent.builder().componentName("HRA").type("EARNING").amount(hra).displayOrder(2).build());
            detailComponents.add(PayrollRunDetailComponent.builder().componentName("Special Allowance").type("EARNING").amount(specialAllowance).displayOrder(3).build());
        }

        BigDecimal additions = BigDecimal.ZERO;
        BigDecimal deductions = computedDeductions;
        int nextOrder = detailComponents.size() + 1;

        if (input.getSalaryInputs() != null) {
            for (SalaryInput si : input.getSalaryInputs()) {
                if ("Addition".equalsIgnoreCase(si.getInputType())) {
                    additions = additions.add(si.getAmount());
                    detailComponents.add(PayrollRunDetailComponent.builder().componentName(si.getPayComponent()).type("EARNING").amount(si.getAmount()).displayOrder(nextOrder++).build());
                } else if ("Deduction".equalsIgnoreCase(si.getInputType())) {
                    deductions = deductions.add(si.getAmount());
                    detailComponents.add(PayrollRunDetailComponent.builder().componentName(si.getPayComponent()).type("DEDUCTION").amount(si.getAmount()).displayOrder(nextOrder++).build());
                } else if ("Override".equalsIgnoreCase(si.getInputType())) {
                    basicPay = si.getAmount();
                    monthlyGross = basicPay.add(hra).add(specialAllowance);
                    // In a real app we'd find "Basic" in detailComponents and update it.
                    for (PayrollRunDetailComponent c : detailComponents) {
                        if ("Basic".equalsIgnoreCase(c.getComponentName()) || "Basic Pay".equalsIgnoreCase(c.getComponentName())) {
                            c.setAmount(basicPay);
                        }
                    }
                }
            }
        }

        // Add Reimbursements
        if (input.getReimbursements() != null) {
            for (ReimbursementClaim rc : input.getReimbursements()) {
                if ("Approved".equalsIgnoreCase(rc.getStatus())) {
                    // Re-imbursements are typically tax-free additions to net pay, treated as earnings.
                    additions = additions.add(rc.getClaimedAmount());
                    detailComponents.add(PayrollRunDetailComponent.builder()
                            .componentName(rc.getReimbursementType() + " Reimbursement")
                            .type("EARNING")
                            .amount(rc.getClaimedAmount())
                            .displayOrder(nextOrder++)
                            .build());
                }
            }
        }

        BigDecimal totalLopDays = BigDecimal.ZERO;
        if (input.getLopDays() != null) {
            for (EmployeeLOP lop : input.getLopDays()) {
                totalLopDays = totalLopDays.add(lop.getLopDays());
            }
        }

        BigDecimal lopDeduction = monthlyGross.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP).multiply(totalLopDays);
        if (lopDeduction.compareTo(BigDecimal.ZERO) > 0) {
            deductions = deductions.add(lopDeduction);
            detailComponents.add(PayrollRunDetailComponent.builder().componentName("Loss of Pay").type("DEDUCTION").amount(lopDeduction).displayOrder(nextOrder++).build());
        }

        // Statutory Deductions
        BigDecimal pfDeduction = BigDecimal.ZERO;
        BigDecimal esiDeduction = BigDecimal.ZERO;
        BigDecimal professionalTax = BigDecimal.valueOf(200);

        if (input.getStatutoryDetails() != null) {
            if (Boolean.TRUE.equals(input.getStatutoryDetails().getPfApplicable())) {
                pfDeduction = basicPay.multiply(BigDecimal.valueOf(0.12)).min(BigDecimal.valueOf(1800)).setScale(2, RoundingMode.HALF_UP);
            }
            if (Boolean.TRUE.equals(input.getStatutoryDetails().getEsiApplicable()) && monthlyGross.compareTo(BigDecimal.valueOf(21000)) <= 0) {
                esiDeduction = monthlyGross.multiply(BigDecimal.valueOf(0.0075)).setScale(2, RoundingMode.HALF_UP);
            }
        }

        BigDecimal tdsDeduction = monthlyGross.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        deductions = deductions.add(pfDeduction).add(esiDeduction).add(tdsDeduction).add(professionalTax);
        
        detailComponents.add(PayrollRunDetailComponent.builder().componentName("Provident Fund").type("DEDUCTION").amount(pfDeduction).displayOrder(nextOrder++).build());
        detailComponents.add(PayrollRunDetailComponent.builder().componentName("ESI").type("DEDUCTION").amount(esiDeduction).displayOrder(nextOrder++).build());
        detailComponents.add(PayrollRunDetailComponent.builder().componentName("TDS").type("DEDUCTION").amount(tdsDeduction).displayOrder(nextOrder++).build());
        detailComponents.add(PayrollRunDetailComponent.builder().componentName("Professional Tax").type("DEDUCTION").amount(professionalTax).displayOrder(nextOrder++).build());

        BigDecimal gross = monthlyGross.add(additions);
        BigDecimal net = gross.subtract(deductions);

        // Apply holds
        if (input.getActiveHold() != null) {
            net = net.subtract(input.getActiveHold().getHoldAmount());
            deductions = deductions.add(input.getActiveHold().getHoldAmount());
            detailComponents.add(PayrollRunDetailComponent.builder().componentName("Salary Hold").type("DEDUCTION").amount(input.getActiveHold().getHoldAmount()).displayOrder(nextOrder++).build());
        }

        return PayrollCalculationResult.builder()
                .basicPay(basicPay)
                .hra(hra)
                .specialAllowance(specialAllowance)
                .pfDeduction(pfDeduction)
                .esiDeduction(esiDeduction)
                .tdsDeduction(tdsDeduction)
                .professionalTax(professionalTax)
                .gross(gross)
                .totalDeductions(deductions)
                .net(net)
                .totalLopDays(totalLopDays)
                .payableDays(BigDecimal.valueOf(30).subtract(totalLopDays))
                .detailComponents(detailComponents)
                .build();
    }
}
