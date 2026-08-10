package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ChallanDto;
import com.grivetyglobals.invoiceiq.dto.finance.ChallanLineItemDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.Challan;
import com.grivetyglobals.invoiceiq.entity.finance.ChallanLineItem;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Client;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ChallanRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import com.grivetyglobals.invoiceiq.repository.sales.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

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
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
            challan.setClient(client);
        } else {
            challan.setClient(null);
        }

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            challan.setProject(project);
        } else {
            challan.setProject(null);
        }

        challan.setChallanNumber(dto.getChallanNumber());
        challan.setEwayBillNo(dto.getEwayBillNo());
        challan.setChallanDate(dto.getChallanDate());
        challan.setDescription(dto.getDescription());
        challan.setRemarks(dto.getRemarks());
        challan.setStatus(dto.getStatus());
        challan.setAttachmentFileId(dto.getAttachmentFileId());
        challan.setAttachmentName(dto.getAttachmentName());
        challan.setTemplateName(dto.getTemplateName());
        challan.setTransportMode(dto.getTransportMode());
        challan.setPoNumber(dto.getPoNumber());
        challan.setPoDate(dto.getPoDate());

        Client client = challan.getClient();
        if (dto.getDeliveryLocation() != null && !dto.getDeliveryLocation().isBlank()) {
            challan.setDeliveryLocation(dto.getDeliveryLocation());
        } else if (client != null) {
            challan.setDeliveryLocation(buildDeliveryLocation(client));
        } else {
            challan.setDeliveryLocation(null);
        }

        if (dto.getBillingAddress() != null && !dto.getBillingAddress().isBlank()) {
            challan.setBillingAddress(dto.getBillingAddress());
        } else if (client != null) {
            challan.setBillingAddress(buildBillingAddress(client));
        } else {
            challan.setBillingAddress(null);
        }

        if (dto.getShippingAddress() != null && !dto.getShippingAddress().isBlank()) {
            challan.setShippingAddress(dto.getShippingAddress());
        } else if (client != null) {
            challan.setShippingAddress(buildShippingAddress(client));
        } else {
            challan.setShippingAddress(null);
        }

        if (dto.getPlaceOfSupply() != null && !dto.getPlaceOfSupply().isBlank()) {
            challan.setPlaceOfSupply(dto.getPlaceOfSupply());
        } else if (client != null) {
            String pos = client.getPlaceOfSupply() != null ? client.getPlaceOfSupply()
                    : (client.getShippingState() != null ? client.getShippingState() : client.getBillingState());
            challan.setPlaceOfSupply(pos);
        } else {
            challan.setPlaceOfSupply(null);
        }

        if (dto.getContactName() != null && !dto.getContactName().isBlank()) {
            challan.setContactName(dto.getContactName());
        } else if (client != null) {
            challan.setContactName(client.getPrimaryContactPerson() != null ? client.getPrimaryContactPerson() : client.getDisplayName());
        } else {
            challan.setContactName(null);
        }

        if (dto.getContactEmail() != null && !dto.getContactEmail().isBlank()) {
            challan.setContactEmail(dto.getContactEmail());
        } else if (client != null) {
            challan.setContactEmail(client.getEmail());
        } else {
            challan.setContactEmail(null);
        }

        if (dto.getContactMobile() != null && !dto.getContactMobile().isBlank()) {
            challan.setContactMobile(dto.getContactMobile());
        } else if (client != null) {
            challan.setContactMobile(client.getPhone());
        } else {
            challan.setContactMobile(null);
        }

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
                String hsn = itemDto.getHsnSac() != null ? itemDto.getHsnSac() : itemDto.getItemHsn();
                item.setItemName(itemDto.getItemName());
                item.setHsnSac(hsn);
                item.setItemHsn(hsn);
                item.setDescription(itemDto.getDescription());
                item.setDispatchedQuantity(itemDto.getDispatchedQuantity());
                item.setUnit(itemDto.getUnit());
            }
        }
    }

    private ChallanDto mapToDto(Challan challan) {
        ChallanDto dto = new ChallanDto();
        dto.setId(challan.getId());
        
        if (challan.getClient() != null) {
            dto.setClientId(challan.getClient().getId());
            dto.setClientName(challan.getClient().getDisplayName() != null 
                    ? challan.getClient().getDisplayName() 
                    : challan.getClient().getCompanyName());
        }
        
        if (challan.getProject() != null) {
            dto.setProjectId(challan.getProject().getId());
            dto.setProjectName(challan.getProject().getProjectName());
        }

        dto.setChallanNumber(challan.getChallanNumber());
        dto.setEwayBillNo(challan.getEwayBillNo());
        dto.setChallanDate(challan.getChallanDate());
        dto.setDescription(challan.getDescription());
        dto.setRemarks(challan.getRemarks());
        dto.setStatus(challan.getStatus());
        dto.setAttachmentFileId(challan.getAttachmentFileId());
        dto.setAttachmentName(challan.getAttachmentName());
        dto.setTemplateName(challan.getTemplateName());
        dto.setTransportMode(challan.getTransportMode());
        dto.setPoNumber(challan.getPoNumber());
        dto.setPoDate(challan.getPoDate());
        dto.setCreatedAt(challan.getCreatedAt());

        Client c = challan.getClient();
        dto.setBillingAddress(challan.getBillingAddress() != null ? challan.getBillingAddress() : buildBillingAddress(c));
        dto.setShippingAddress(challan.getShippingAddress() != null ? challan.getShippingAddress() : buildShippingAddress(c));
        dto.setDeliveryLocation(challan.getDeliveryLocation() != null ? challan.getDeliveryLocation() : buildDeliveryLocation(c));
        
        if (challan.getPlaceOfSupply() != null) {
            dto.setPlaceOfSupply(challan.getPlaceOfSupply());
        } else if (c != null) {
            dto.setPlaceOfSupply(c.getPlaceOfSupply() != null ? c.getPlaceOfSupply()
                    : (c.getShippingState() != null ? c.getShippingState() : c.getBillingState()));
        }

        if (challan.getContactName() != null) {
            dto.setContactName(challan.getContactName());
        } else if (c != null) {
            dto.setContactName(c.getPrimaryContactPerson() != null ? c.getPrimaryContactPerson() : c.getDisplayName());
        }

        if (challan.getContactEmail() != null) {
            dto.setContactEmail(challan.getContactEmail());
        } else if (c != null) {
            dto.setContactEmail(c.getEmail());
        }

        if (challan.getContactMobile() != null) {
            dto.setContactMobile(challan.getContactMobile());
        } else if (c != null) {
            dto.setContactMobile(c.getPhone());
        }

        if (challan.getLineItems() != null) {
            dto.setLineItems(challan.getLineItems().stream().map(item -> {
                ChallanLineItemDto itemDto = new ChallanLineItemDto();
                itemDto.setId(item.getId());
                itemDto.setItemName(item.getItemName());
                String hsn = item.getHsnSac() != null ? item.getHsnSac() : item.getItemHsn();
                itemDto.setHsnSac(hsn);
                itemDto.setItemHsn(hsn);
                itemDto.setDescription(item.getDescription());
                itemDto.setDispatchedQuantity(item.getDispatchedQuantity());
                itemDto.setUnit(item.getUnit());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        
        return dto;
    }

    private String buildDeliveryLocation(Client c) {
        if (c == null) return null;
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (c.getShippingAddressLine1() != null && !c.getShippingAddressLine1().isBlank()) {
            parts.add(c.getShippingAddressLine1());
            if (c.getShippingAddressLine2() != null && !c.getShippingAddressLine2().isBlank()) parts.add(c.getShippingAddressLine2());
            if (c.getShippingCity() != null && !c.getShippingCity().isBlank()) parts.add(c.getShippingCity());
            if (c.getShippingState() != null && !c.getShippingState().isBlank()) parts.add(c.getShippingState());
            if (c.getShippingPinCode() != null && !c.getShippingPinCode().isBlank()) parts.add(c.getShippingPinCode());
            if (c.getShippingCountry() != null && !c.getShippingCountry().isBlank()) parts.add(c.getShippingCountry());
        } else if (c.getBillingAddressLine1() != null && !c.getBillingAddressLine1().isBlank()) {
            parts.add(c.getBillingAddressLine1());
            if (c.getBillingAddressLine2() != null && !c.getBillingAddressLine2().isBlank()) parts.add(c.getBillingAddressLine2());
            if (c.getBillingCity() != null && !c.getBillingCity().isBlank()) parts.add(c.getBillingCity());
            if (c.getBillingState() != null && !c.getBillingState().isBlank()) parts.add(c.getBillingState());
            if (c.getBillingPinCode() != null && !c.getBillingPinCode().isBlank()) parts.add(c.getBillingPinCode());
            if (c.getBillingCountry() != null && !c.getBillingCountry().isBlank()) parts.add(c.getBillingCountry());
        }
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private String buildShippingAddress(Client c) {
        if (c == null) return null;
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (c.getDisplayName() != null) parts.add(c.getDisplayName());
        if (c.getShippingAddressLine1() != null && !c.getShippingAddressLine1().isBlank()) {
            parts.add(c.getShippingAddressLine1());
            if (c.getShippingAddressLine2() != null && !c.getShippingAddressLine2().isBlank()) parts.add(c.getShippingAddressLine2());
            String cityStatePin = java.util.stream.Stream.of(c.getShippingCity(), c.getShippingState(), c.getShippingPinCode())
                    .filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
            if (!cityStatePin.isBlank()) parts.add(cityStatePin);
            if (c.getShippingCountry() != null && !c.getShippingCountry().isBlank()) parts.add(c.getShippingCountry());
        } else if (c.getBillingAddressLine1() != null && !c.getBillingAddressLine1().isBlank()) {
            parts.add(c.getBillingAddressLine1());
            if (c.getBillingAddressLine2() != null && !c.getBillingAddressLine2().isBlank()) parts.add(c.getBillingAddressLine2());
            String cityStatePin = java.util.stream.Stream.of(c.getBillingCity(), c.getBillingState(), c.getBillingPinCode())
                    .filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
            if (!cityStatePin.isBlank()) parts.add(cityStatePin);
            if (c.getBillingCountry() != null && !c.getBillingCountry().isBlank()) parts.add(c.getBillingCountry());
        }
        return parts.isEmpty() ? null : String.join("\n", parts);
    }

    private String buildBillingAddress(Client c) {
        if (c == null) return null;
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (c.getDisplayName() != null) parts.add(c.getDisplayName());
        if (c.getBillingAddressLine1() != null && !c.getBillingAddressLine1().isBlank()) {
            parts.add(c.getBillingAddressLine1());
            if (c.getBillingAddressLine2() != null && !c.getBillingAddressLine2().isBlank()) parts.add(c.getBillingAddressLine2());
            String cityStatePin = java.util.stream.Stream.of(c.getBillingCity(), c.getBillingState(), c.getBillingPinCode())
                    .filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
            if (!cityStatePin.isBlank()) parts.add(cityStatePin);
            if (c.getBillingCountry() != null && !c.getBillingCountry().isBlank()) parts.add(c.getBillingCountry());
        }
        return parts.isEmpty() ? null : String.join("\n", parts);
    }
}
