package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.DepartmentRequest;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.repository.DepartmentRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
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

    @Transactional
    public Department createDepartment(DepartmentRequest request, UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department parentDepartment = null;
        if (request.getParentDepartmentId() != null) {
            parentDepartment = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Parent Department not found"));
        }

        Department department = Department.builder()
                .organization(organization)
                .departmentCode(request.getDepartmentCode())
                .departmentName(request.getDepartmentName())
                .parentDepartment(parentDepartment)
                .description(request.getDescription())
                .build();

        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments(UUID organizationId) {
        return departmentRepository.findAll().stream()
                .filter(d -> d.getOrganization().getId().equals(organizationId))
                .toList();
    }

    public Department getDepartmentById(UUID id, UUID organizationId) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!department.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access Denied: Department belongs to another organization");
        }

        return department;
    }

    @Transactional
    public Department updateDepartment(UUID id, DepartmentRequest request, UUID organizationId) {
        Department department = getDepartmentById(id, organizationId);

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

    @Transactional
    public void deleteDepartment(UUID id, UUID organizationId) {
        Department department = getDepartmentById(id, organizationId);
        departmentRepository.delete(department);
    }
}
