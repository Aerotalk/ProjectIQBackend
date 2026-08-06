package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for saving an employee's bank account details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBankAccountRequest {

    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;
    private String accountType;
    private String accountHolderName;
    private String paymentMode;
    private Boolean primaryAccount;
}
