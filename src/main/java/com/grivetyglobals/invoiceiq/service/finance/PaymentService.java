package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PaymentDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.Payment;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CompanyRepository companyRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByCompany(UUID companyId) {
        return paymentRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentDto getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toDto(payment);
    }

    public PaymentDto createPayment(UUID companyId, PaymentDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Payment payment = paymentMapper.toEntity(dto);
        payment.setCompany(company);

        if (payment.getPaymentId() == null || payment.getPaymentId().isEmpty()) {
            payment.setPaymentId("PAY-" + System.currentTimeMillis());
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    public PaymentDto updatePayment(UUID id, PaymentDto dto) {
        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        paymentMapper.updateEntityFromDto(dto, existing);
        Payment updated = paymentRepository.save(existing);
        return paymentMapper.toDto(updated);
    }

    public void deletePayment(UUID id) {
        paymentRepository.deleteById(id);
    }
}
