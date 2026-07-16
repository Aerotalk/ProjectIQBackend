package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.DepartmentRequest;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.repository.DepartmentRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import com.grivetyglobals.invoiceiq.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;

    @org.springframework.cache.annotation.CacheEvict(value = "departmentsList", allEntries = true)
    @Transactional
    public Department createDepartment(DepartmentRequest request) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();

        Organization organization = organizationRepository.findById(currentOrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department parentDepartment = null;
        if (request.getParentDepartmentId() != null) {
            parentDepartment = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Parent Department not found"));
        }

        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
        Company company = null;
        if (currentCompanyId != null) {
            company = companyRepository.findById(currentCompanyId).orElse(null);
        }

        Department department = Department.builder()
                .organization(organization)
                .company(company)
                .departmentCode(request.getDepartmentCode())
                .departmentName(request.getDepartmentName())
                .parentDepartment(parentDepartment)
                .description(request.getDescription())
                .build();

        return departmentRepository.save(department);
    }

    @org.springframework.cache.annotation.Cacheable(value = "departmentsList", key = "T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentOrganizationId() + '-' + T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentCompanyId()")
    public List<Department> getAllDepartments() {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
        return departmentRepository.findByOrganizationIdAndCompanyId(currentOrgId, currentCompanyId);
    }

    public Department getDepartmentById(UUID id) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!department.getOrganization().getId().equals(currentOrgId)) {
            throw new RuntimeException("Access Denied: Department belongs to another organization");
        }

        if (currentCompanyId != null && department.getCompany() != null
                && !department.getCompany().getId().equals(currentCompanyId)) {
            throw new RuntimeException("Access Denied: Department belongs to another company");
        }

        return department;
    }

    @org.springframework.cache.annotation.CacheEvict(value = "departmentsList", allEntries = true)
    @Transactional
    public Department updateDepartment(UUID id, DepartmentRequest request) {
        Department department = getDepartmentById(id);

        Department parentDepartment = null;
        if (request.getParentDepartmentId() != null) {
            parentDepartment = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Parent Department not found"));
        }

        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        department.setParentDepartment(parentDepartment);
        department.setDescription(request.getDescription());

        return departmentRepository.save(department);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "departmentsList", allEntries = true)
    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
