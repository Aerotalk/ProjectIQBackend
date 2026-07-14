package com.grivetyglobals.invoiceiq.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallanDto {
    private UUID id;
    private UUID vendorId;
    private String vendorName;
    private UUID projectId;
    private String projectName;
    private String challanNumber;
    private LocalDate challanDate;
    private String remarks;
    private String status;
    private UUID attachmentFileId;
}
