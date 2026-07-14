package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.PurchaseOrderDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder;
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
        po.setAmount(dto.getAmount());
        po.setRemarks(dto.getRemarks());
        po.setStatus(dto.getStatus());
        po.setAttachmentFileId(dto.getAttachmentFileId());
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
        dto.setAmount(po.getAmount());
        dto.setRemarks(po.getRemarks());
        dto.setStatus(po.getStatus());
        dto.setAttachmentFileId(po.getAttachmentFileId());
        
        return dto;
    }
}
