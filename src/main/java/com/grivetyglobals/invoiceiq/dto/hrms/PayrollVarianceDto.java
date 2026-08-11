package com.grivetyglobals.invoiceiq.dto.hrms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollVarianceDto {
    private String component;
    private String previous;
    private String current;
    private String difference;
    private String reason;
}
