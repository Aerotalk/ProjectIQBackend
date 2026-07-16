package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PaymentDto;
import com.grivetyglobals.invoiceiq.entity.finance.Payment;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public PaymentDto toDto(Payment entity) {
        if (entity == null) return null;

        return PaymentDto.builder()
                .id(entity.getId())
                .paymentId(entity.getPaymentId())
                .projectId(entity.getProject() != null ? entity.getProject().getId() : null)
                .projectName(entity.getProjectName())
                .paymentDate(entity.getPaymentDate() != null ? entity.getPaymentDate().format(dateFormatter) : null)
                .amountPaid(entity.getAmountPaid())
                .paymentMethod(entity.getPaymentMethod())
                .referenceId(entity.getReferenceId())
                .notes(entity.getNotes())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    public Payment toEntity(PaymentDto dto) {
        if (dto == null) return null;

        Payment payment = Payment.builder()
                .id(dto.getId())
                .paymentId(dto.getPaymentId())
                .projectName(dto.getProjectName())
                .paymentDate(dto.getPaymentDate() != null ? LocalDate.parse(dto.getPaymentDate(), dateFormatter) : null)
                .amountPaid(dto.getAmountPaid())
                .paymentMethod(dto.getPaymentMethod())
                .referenceId(dto.getReferenceId())
                .notes(dto.getNotes())
                .status(dto.getStatus())
                .build();

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            payment.setProject(project);
        }

        return payment;
    }

    public void updateEntityFromDto(PaymentDto dto, Payment entity) {
        if (dto.getProjectName() != null) entity.setProjectName(dto.getProjectName());
        if (dto.getPaymentDate() != null) entity.setPaymentDate(LocalDate.parse(dto.getPaymentDate(), dateFormatter));
        if (dto.getAmountPaid() != null) entity.setAmountPaid(dto.getAmountPaid());
        if (dto.getPaymentMethod() != null) entity.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getReferenceId() != null) entity.setReferenceId(dto.getReferenceId());
        if (dto.getNotes() != null) entity.setNotes(dto.getNotes());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            entity.setProject(project);
        } else {
            entity.setProject(null);
        }
    }
}
