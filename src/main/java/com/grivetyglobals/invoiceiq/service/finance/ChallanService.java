package com.grivetyglobals.invoiceiq.service.finance;

import com.grivetyglobals.invoiceiq.dto.finance.ChallanDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.Challan;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ChallanRepository;
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

    public List<ChallanDto> getChallansByCompany(UUID companyId) {
        return challanRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

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

        challan.setChallanNumber(dto.getChallanNumber());
        challan.setChallanDate(dto.getChallanDate());
        challan.setRemarks(dto.getRemarks());
        challan.setStatus(dto.getStatus());
        challan.setAttachmentFileId(dto.getAttachmentFileId());
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
        dto.setChallanDate(challan.getChallanDate());
        dto.setRemarks(challan.getRemarks());
        dto.setStatus(challan.getStatus());
        dto.setAttachmentFileId(challan.getAttachmentFileId());
        
        return dto;
    }
}
