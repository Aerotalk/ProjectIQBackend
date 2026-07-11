package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.DesignationRequest;
import com.grivetyglobals.invoiceiq.entity.Designation;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.repository.DesignationRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesignationService {

    private final DesignationRepository designationRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public Designation createDesignation(DesignationRequest request) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Organization organization = organizationRepository.findById(currentOrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Designation designation = Designation.builder()
                .organization(organization)
                .designationCode(request.getDesignationCode())
                .designationName(request.getDesignationName())
                .hierarchyLevel(request.getHierarchyLevel())
                .description(request.getDescription())
                .build();

        return designationRepository.save(designation);
    }

    public List<Designation> getAllDesignations() {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        return designationRepository.findByOrganizationId(currentOrgId);
    }

    public Designation getDesignationById(UUID id) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        if (!designation.getOrganization().getId().equals(currentOrgId)) {
            throw new RuntimeException("Access Denied: Designation belongs to another organization");
        }

        return designation;
    }

    @Transactional
    public Designation updateDesignation(UUID id, DesignationRequest request) {
        Designation designation = getDesignationById(id);

        designation.setDesignationCode(request.getDesignationCode());
        designation.setDesignationName(request.getDesignationName());
        designation.setHierarchyLevel(request.getHierarchyLevel());
        designation.setDescription(request.getDescription());

        return designationRepository.save(designation);
    }

    @Transactional
    public void deleteDesignation(UUID id) {
        Designation designation = getDesignationById(id);
        designationRepository.delete(designation);
    }
}
