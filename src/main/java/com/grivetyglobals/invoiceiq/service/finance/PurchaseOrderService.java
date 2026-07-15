package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PurchaseOrderDto;
import com.grivetyglobals.invoiceiq.dto.finance.PurchaseOrderLineItemDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder;
import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrderLineItem;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.PurchaseOrderRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import com.grivetyglobals.invoiceiq.repository.sales.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CompanyRepository companyRepository;
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;

    public List<PurchaseOrderDto> getPurchaseOrdersByCompany(UUID companyId) {
        return purchaseOrderRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PurchaseOrderDto getPurchaseOrder(UUID id) {
        return mapToDto(purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found")));
    }

    @Transactional
    public PurchaseOrderDto createPurchaseOrder(UUID companyId, PurchaseOrderDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        PurchaseOrder po = new PurchaseOrder();
        po.setCompany(company);
        mapToEntity(dto, po);

        return mapToDto(purchaseOrderRepository.save(po));
    }

    @Transactional
    public PurchaseOrderDto updatePurchaseOrder(UUID id, PurchaseOrderDto dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));

        mapToEntity(dto, po);

        return mapToDto(purchaseOrderRepository.save(po));
    }

    @Transactional
    public void deletePurchaseOrder(UUID id) {
        purchaseOrderRepository.deleteById(id);
    }

    private void mapToEntity(PurchaseOrderDto dto, PurchaseOrder po) {
        if (dto.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(dto.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
            po.setVendor(vendor);
        } else {
            po.setVendor(null);
        }

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            po.setProject(project);
        } else {
            po.setProject(null);
        }

        po.setPoNumber(dto.getPoNumber());
        po.setPoDate(dto.getPoDate());
        po.setGrandTotal(dto.getGrandTotal());
        po.setDescription(dto.getDescription());
        po.setInternalNotes(dto.getInternalNotes());
        po.setExpectedDelivery(dto.getExpectedDelivery());
        po.setStatus(dto.getStatus());
        po.setAttachmentFileId(dto.getAttachmentFileId());
        po.setAttachmentName(dto.getAttachmentName());

        if (po.getLineItems() == null) {
            po.setLineItems(new java.util.ArrayList<>());
        }
        po.getLineItems().clear();
        if (dto.getLineItems() != null) {
            for (PurchaseOrderLineItemDto itemDto : dto.getLineItems()) {
                PurchaseOrderLineItem item = new PurchaseOrderLineItem();
                item.setPurchaseOrder(po);
                item.setProductId(itemDto.getProductId());
                item.setItemName(itemDto.getItemName());
                item.setDescription(itemDto.getDescription());
                item.setQuantity(itemDto.getQuantity());
                item.setUnit(itemDto.getUnit());
                item.setRate(itemDto.getRate());
                item.setDiscount(itemDto.getDiscount());
                item.setTaxableAmount(itemDto.getTaxableAmount());
                item.setGstRate(itemDto.getGstRate());
                item.setGstAmount(itemDto.getGstAmount());
                item.setTotalAmount(itemDto.getTotalAmount());
                po.getLineItems().add(item);
            }
        }
    }

    private PurchaseOrderDto mapToDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        
        if (po.getVendor() != null) {
            dto.setVendorId(po.getVendor().getId());
            dto.setVendorName(po.getVendor().getCompanyName());
        }
        
        if (po.getProject() != null) {
            dto.setProjectId(po.getProject().getId());
            dto.setProjectName(po.getProject().getProjectName());
        }

        dto.setPoNumber(po.getPoNumber());
        dto.setPoDate(po.getPoDate());
        dto.setGrandTotal(po.getGrandTotal());
        dto.setDescription(po.getDescription());
        dto.setInternalNotes(po.getInternalNotes());
        dto.setExpectedDelivery(po.getExpectedDelivery());
        dto.setStatus(po.getStatus());
        dto.setAttachmentFileId(po.getAttachmentFileId());
        dto.setAttachmentName(po.getAttachmentName());

        if (po.getLineItems() != null) {
            dto.setLineItems(po.getLineItems().stream().map(item -> {
                PurchaseOrderLineItemDto itemDto = new PurchaseOrderLineItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProductId());
                itemDto.setItemName(item.getItemName());
                itemDto.setDescription(item.getDescription());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnit(item.getUnit());
                itemDto.setRate(item.getRate());
                itemDto.setDiscount(item.getDiscount());
                itemDto.setTaxableAmount(item.getTaxableAmount());
                itemDto.setGstRate(item.getGstRate());
                itemDto.setGstAmount(item.getGstAmount());
                itemDto.setTotalAmount(item.getTotalAmount());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        
        return dto;
    }
}
