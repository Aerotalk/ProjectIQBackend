package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyBankAccountDto {
    private String bankName;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String swiftCode;
    private String upiId;
    private Boolean isPrimary;
}
