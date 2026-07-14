package com.grivetyglobals.invoiceiq.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBankDetailsDto {
    private UUID id;
    private String accountName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String branchName;
    private String swiftCode;
}
