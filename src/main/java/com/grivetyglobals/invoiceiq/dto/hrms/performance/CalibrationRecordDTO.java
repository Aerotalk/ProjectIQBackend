package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.hrms.performance.CalibrationRecord;
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
public class CalibrationRecordDTO {
    private UUID id;
    
    private EmployeeSummaryDTO employee;
    private SelfReviewDTO.CycleSummaryDTO cycle;
    
    private BigDecimal currentRating;
    private BigDecimal proposedRating;
    private BigDecimal finalRating;
    private String reviewer;
    private String status;

    public static CalibrationRecordDTO fromEntity(CalibrationRecord record) {
        if (record == null) {
            return null;
        }

        SelfReviewDTO.CycleSummaryDTO cycleSummary = null;
        if (record.getCycle() != null) {
            cycleSummary = SelfReviewDTO.CycleSummaryDTO.builder()
                    .id(record.getCycle().getId())
                    .name(record.getCycle().getName())
                    .build();
        }

        return CalibrationRecordDTO.builder()
                .id(record.getId())
                .employee(EmployeeSummaryDTO.fromEntity(record.getEmployee()))
                .cycle(cycleSummary)
                .currentRating(record.getCurrentRating())
                .proposedRating(record.getProposedRating())
                .finalRating(record.getFinalRating())
                .reviewer(record.getReviewer())
                .status(record.getStatus())
                .build();
    }
}
