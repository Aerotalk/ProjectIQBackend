package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ChallanDto;
import com.grivetyglobals.invoiceiq.dto.finance.ChallanLineItemDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.Challan;
import com.grivetyglobals.invoiceiq.entity.finance.ChallanLineItem;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ChallanRepository;
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
public class ChallanService {

    private final ChallanRepository challanRepository;
    private final CompanyRepository companyRepository;
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public List<ChallanDto> getChallansByCompany(UUID companyId) {
        return challanRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChallanDto getChallan(UUID id) {
        return mapToDto(challanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found")));
    }

    @Transactional
    public ChallanDto createChallan(UUID companyId, ChallanDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        Challan challan = new Challan();
        challan.setCompany(company);
        mapToEntity(dto, challan);

        return mapToDto(challanRepository.save(challan));
    }

    @Transactional
    public ChallanDto updateChallan(UUID id, ChallanDto dto) {
        Challan challan = challanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found"));

        mapToEntity(dto, challan);

        return mapToDto(challanRepository.save(challan));
    }

    @Transactional
    public void deleteChallan(UUID id) {
        challanRepository.deleteById(id);
    }

    private void mapToEntity(ChallanDto dto, Challan challan) {
        if (dto.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(dto.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
            challan.setVendor(vendor);
        } else {
            challan.setVendor(null);
        }

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            challan.setProject(project);
        } else {
            challan.setProject(null);
        }

        if (dto.getLinkedVendorPoId() != null) {
            com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder po = purchaseOrderRepository.findById(dto.getLinkedVendorPoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));
            if (!po.getCompany().getId().equals(challan.getCompany().getId())) {
                throw new ResourceNotFoundException("Purchase Order not found");
            }
            challan.setLinkedVendorPo(po);
        } else {
            challan.setLinkedVendorPo(null);
        }

        challan.setChallanNumber(dto.getChallanNumber());
        challan.setEwayBillNo(dto.getEwayBillNo());
        challan.setChallanDate(dto.getChallanDate());
        challan.setDescription(dto.getDescription());
        challan.setRemarks(dto.getRemarks());
        challan.setStatus(dto.getStatus());
        challan.setAttachmentFileId(dto.getAttachmentFileId());
        challan.setAttachmentName(dto.getAttachmentName());
        challan.setTransportMode(dto.getTransportMode());
        challan.setDeliveryLocation(dto.getDeliveryLocation());
        challan.setPlaceOfSupply(dto.getPlaceOfSupply());
        challan.setContactName(dto.getContactName());
        challan.setContactEmail(dto.getContactEmail());
        challan.setContactMobile(dto.getContactMobile());
        challan.setPoNumber(dto.getPoNumber());
        challan.setPoDate(dto.getPoDate());

        if (challan.getLineItems() == null) {
            challan.setLineItems(new java.util.ArrayList<>());
        }
        
        if (dto.getLineItems() == null || dto.getLineItems().isEmpty()) {
            challan.getLineItems().clear();
        } else {
            java.util.Set<UUID> incomingIds = dto.getLineItems().stream()
                    .map(ChallanLineItemDto::getId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
            
            challan.getLineItems().removeIf(item -> item.getId() != null && !incomingIds.contains(item.getId()));

            java.util.Map<UUID, ChallanLineItem> existingMap = challan.getLineItems().stream()
                    .filter(i -> i.getId() != null)
                    .collect(Collectors.toMap(ChallanLineItem::getId, i -> i));

            for (ChallanLineItemDto itemDto : dto.getLineItems()) {
                ChallanLineItem item = null;
                if (itemDto.getId() != null) {
                    item = existingMap.get(itemDto.getId());
                    if (item == null) {
                        throw new ResourceNotFoundException("Line item not found or does not belong to this challan: " + itemDto.getId());
                    }
                } else {
                    item = new ChallanLineItem();
                    item.setChallan(challan);
                    challan.getLineItems().add(item);
                }
                item.setDescription(itemDto.getDescription());
                item.setItemName(itemDto.getItemName());
                item.setItemHsn(itemDto.getItemHsn());
                item.setDispatchedQuantity(itemDto.getDispatchedQuantity());
                item.setUnit(itemDto.getUnit());
            }
        }
    }

    private ChallanDto mapToDto(Challan challan) {
        ChallanDto dto = new ChallanDto();
        dto.setId(challan.getId());
        
        if (challan.getVendor() != null) {
            dto.setVendorId(challan.getVendor().getId());
            dto.setVendorName(challan.getVendor().getCompanyName());
        }
        
        if (challan.getProject() != null) {
            dto.setProjectId(challan.getProject().getId());
            dto.setProjectName(challan.getProject().getProjectName());
        }

        dto.setChallanNumber(challan.getChallanNumber());
        dto.setEwayBillNo(challan.getEwayBillNo());
        dto.setChallanDate(challan.getChallanDate());
        dto.setDescription(challan.getDescription());
        
        if (challan.getLinkedVendorPo() != null) {
            dto.setLinkedVendorPoId(challan.getLinkedVendorPo().getId());
            dto.setLinkedVendorPoNumber(challan.getLinkedVendorPo().getPoNumber());
        }
        dto.setRemarks(challan.getRemarks());
        dto.setStatus(challan.getStatus());
        dto.setAttachmentFileId(challan.getAttachmentFileId());
        dto.setAttachmentName(challan.getAttachmentName());
        dto.setTransportMode(challan.getTransportMode());
        dto.setDeliveryLocation(challan.getDeliveryLocation());
        dto.setPlaceOfSupply(challan.getPlaceOfSupply());
        dto.setContactName(challan.getContactName());
        dto.setContactEmail(challan.getContactEmail());
        dto.setContactMobile(challan.getContactMobile());
        dto.setPoNumber(challan.getPoNumber());
        dto.setPoDate(challan.getPoDate());

        if (challan.getLineItems() != null) {
            dto.setLineItems(challan.getLineItems().stream().map(item -> {
                ChallanLineItemDto itemDto = new ChallanLineItemDto();
                itemDto.setId(item.getId());
                itemDto.setItemName(item.getItemName());
                itemDto.setItemHsn(item.getItemHsn());
                itemDto.setDescription(item.getDescription());
                itemDto.setDispatchedQuantity(item.getDispatchedQuantity());
                itemDto.setUnit(item.getUnit());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        
        return dto;
    }
}
