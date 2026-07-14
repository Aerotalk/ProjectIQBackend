package com.grivetyglobals.invoiceiq.service.sales;

import com.grivetyglobals.invoiceiq.dto.sales.QuotationDto;
import com.grivetyglobals.invoiceiq.dto.sales.QuotationLineItemDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.sales.Client;
import com.grivetyglobals.invoiceiq.entity.sales.Product;
import com.grivetyglobals.invoiceiq.entity.sales.Quotation;
import com.grivetyglobals.invoiceiq.entity.sales.QuotationLineItem;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ClientRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ProductRepository;
import com.grivetyglobals.invoiceiq.repository.sales.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CompanyRepository companyRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public List<QuotationDto> getQuotationsByCompany(UUID companyId) {
        return quotationRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public QuotationDto getQuotation(UUID id) {
        return mapToDto(quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found")));
    }

    @Transactional
    public QuotationDto createQuotation(UUID companyId, QuotationDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Quotation quotation = new Quotation();
        quotation.setCompany(company);
        quotation.setClient(client);
        mapToEntity(dto, quotation);
        
        handleLineItems(dto, quotation);

        return mapToDto(quotationRepository.save(quotation));
    }

    @Transactional
    public QuotationDto updateQuotation(UUID id, QuotationDto dto) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));

        if (!quotation.getClient().getId().equals(dto.getClientId())) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
            quotation.setClient(client);
        }

        mapToEntity(dto, quotation);

        if (quotation.getLineItems() != null) {
            quotation.getLineItems().clear();
        }

        handleLineItems(dto, quotation);

        return mapToDto(quotationRepository.save(quotation));
    }

    @Transactional
    public void deleteQuotation(UUID id) {
        quotationRepository.deleteById(id);
    }

    private void handleLineItems(QuotationDto dto, Quotation quotation) {
        if (dto.getLineItems() != null) {
            List<QuotationLineItem> items = dto.getLineItems().stream().map(i -> {
                Product product = productRepository.findById(i.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                
                QuotationLineItem item = new QuotationLineItem();
                item.setQuotation(quotation);
                item.setProduct(product);
                item.setItemName(i.getItemName());
                item.setDescription(i.getDescription());
                item.setQuantity(i.getQuantity());
                item.setUnit(i.getUnit());
                item.setRate(i.getRate());
                item.setDiscount(i.getDiscount());
                item.setGstRate(i.getGstRate());
                item.setTaxableAmount(i.getTaxableAmount());
                item.setGstAmount(i.getGstAmount());
                item.setTotalAmount(i.getTotalAmount());
                return item;
            }).collect(Collectors.toList());
            
            if (quotation.getLineItems() == null) {
                quotation.setLineItems(items);
            } else {
                quotation.getLineItems().addAll(items);
            }
        }
    }

    private void mapToEntity(QuotationDto dto, Quotation quotation) {
        quotation.setQuotationNo(dto.getQuotationNo());
        quotation.setClientName(dto.getClientName());
        quotation.setDate(dto.getDate());
        quotation.setValidUntil(dto.getValidUntil());
        quotation.setSubject(dto.getSubject());
        quotation.setReference(dto.getReference());
        quotation.setSubTotal(dto.getSubTotal());
        quotation.setTotalDiscount(dto.getTotalDiscount());
        quotation.setTotalTaxableAmount(dto.getTotalTaxableAmount());
        quotation.setTotalGstAmount(dto.getTotalGstAmount());
        quotation.setGrandTotal(dto.getGrandTotal());
        quotation.setNotes(dto.getNotes());
        quotation.setTermsAndConditions(dto.getTermsAndConditions());
        quotation.setStatus(dto.getStatus());
        quotation.setApprovedBy(dto.getApprovedBy());
        quotation.setApprovalDate(dto.getApprovalDate());
        quotation.setWoPoDocumentUrl(dto.getWoPoDocumentUrl());
    }

    private QuotationDto mapToDto(Quotation quotation) {
        QuotationDto dto = new QuotationDto();
        dto.setId(quotation.getId());
        dto.setQuotationNo(quotation.getQuotationNo());
        dto.setClientId(quotation.getClient().getId());
        dto.setClientName(quotation.getClientName());
        dto.setDate(quotation.getDate());
        dto.setValidUntil(quotation.getValidUntil());
        dto.setSubject(quotation.getSubject());
        dto.setReference(quotation.getReference());
        dto.setSubTotal(quotation.getSubTotal());
        dto.setTotalDiscount(quotation.getTotalDiscount());
        dto.setTotalTaxableAmount(quotation.getTotalTaxableAmount());
        dto.setTotalGstAmount(quotation.getTotalGstAmount());
        dto.setGrandTotal(quotation.getGrandTotal());
        dto.setNotes(quotation.getNotes());
        dto.setTermsAndConditions(quotation.getTermsAndConditions());
        dto.setStatus(quotation.getStatus());
        dto.setApprovedBy(quotation.getApprovedBy());
        dto.setApprovalDate(quotation.getApprovalDate());
        dto.setWoPoDocumentUrl(quotation.getWoPoDocumentUrl());

        if (quotation.getLineItems() != null) {
            dto.setLineItems(quotation.getLineItems().stream().map(i -> 
                QuotationLineItemDto.builder()
                    .id(i.getId())
                    .productId(i.getProduct().getId())
                    .itemName(i.getItemName())
                    .description(i.getDescription())
                    .quantity(i.getQuantity())
                    .unit(i.getUnit())
                    .rate(i.getRate())
                    .discount(i.getDiscount())
                    .gstRate(i.getGstRate())
                    .taxableAmount(i.getTaxableAmount())
                    .gstAmount(i.getGstAmount())
                    .totalAmount(i.getTotalAmount())
                    .build()
            ).collect(Collectors.toList()));
        }

        return dto;
    }
}
