package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.DesignationRequest;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Designation;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;

    @org.springframework.cache.annotation.CacheEvict(value = "designationsList", allEntries = true)
    @Transactional
    public Designation createDesignation(DesignationRequest request) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();

        Organization organization = organizationRepository.findById(currentOrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Company resolution: request.companyId (org admin dropdown) takes priority,
        // then fall back to the JWT-scoped companyId (company-level user)
        UUID resolvedCompanyId = request.getCompanyId() != null
                ? request.getCompanyId()
                : SecurityUtils.getCurrentCompanyId();

        Company company = null;
        if (resolvedCompanyId != null) {
            company = companyRepository.findById(resolvedCompanyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            // Tenancy guard: company must belong to this org
            if (!company.getOrganization().getId().equals(currentOrgId)) {
                throw new RuntimeException("Access Denied: Company belongs to another organization");
            }
        }

        Designation designation = Designation.builder()
                .organization(organization)
                .company(company)
                .designationCode(request.getDesignationCode())
                .designationName(request.getDesignationName())
                .hierarchyLevel(request.getHierarchyLevel())
                .description(request.getDescription())
                .build();

        return designationRepository.save(designation);
    }

    @org.springframework.cache.annotation.Cacheable(value = "designationsList", key = "T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentOrganizationId() + '-' + (#companyId != null ? #companyId : T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentCompanyId())")
    public List<Designation> getAllDesignations(UUID companyId) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID resolvedCompanyId = companyId != null ? companyId : SecurityUtils.getCurrentCompanyId();
        return designationRepository.findByOrganizationIdAndCompanyId(currentOrgId, resolvedCompanyId);
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

    @org.springframework.cache.annotation.CacheEvict(value = "designationsList", allEntries = true)
    @Transactional
    public Designation updateDesignation(UUID id, DesignationRequest request) {
        Designation designation = getDesignationById(id);

        designation.setDesignationCode(request.getDesignationCode());
        designation.setDesignationName(request.getDesignationName());
        designation.setHierarchyLevel(request.getHierarchyLevel());
        designation.setDescription(request.getDescription());

        return designationRepository.save(designation);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "designationsList", allEntries = true)
    @Transactional
    public void deleteDesignation(UUID id) {
        Designation designation = getDesignationById(id);
        designationRepository.delete(designation);
    }
}
