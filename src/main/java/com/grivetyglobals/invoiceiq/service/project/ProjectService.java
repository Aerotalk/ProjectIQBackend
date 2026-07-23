package com.grivetyglobals.invoiceiq.service.project;

import com.grivetyglobals.invoiceiq.dto.project.ProjectDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public List<ProjectDto> getProjectsByCompany(UUID companyId) {
        return projectRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto getProject(UUID id) {
        return mapToDto(projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found")));
    }

    @Transactional
    public ProjectDto createProject(UUID companyId, ProjectDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (dto.getProjectCode() == null || dto.getProjectCode().trim().isEmpty()) {
            long count = projectRepository.countByCompanyId(companyId);
            dto.setProjectCode(String.format("PROJ-%04d", count + 1));
        }

        Project project = new Project();
        project.setCompany(company);
        mapToEntity(dto, project);

        return mapToDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(UUID id, ProjectDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        mapToEntity(dto, project);

        return mapToDto(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }

    private void mapToEntity(ProjectDto dto, Project project) {
        project.setProjectCode(dto.getProjectCode());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setClient(dto.getClient());
        project.setProjectManager(dto.getProjectManager());
        project.setLinkedQuotation(dto.getLinkedQuotation());
        project.setStartDate(dto.getStartDate());
        project.setExpectedEndDate(dto.getExpectedEndDate());
        project.setExpectedRevenue(dto.getExpectedRevenue());
        project.setBudget(dto.getBudget());
        project.setAssignedVendors(dto.getAssignedVendors());
        project.setAssignedEntities(dto.getAssignedEntities());
        project.setLinkedIncidents(dto.getLinkedIncidents());
        project.setLinkedQuotations(dto.getLinkedQuotations());
        project.setLinkedPOs(dto.getLinkedPOs());
        project.setLinkedExpenses(dto.getLinkedExpenses());
        project.setProjectNotes(dto.getProjectNotes());
        project.setProjectDocuments(dto.getProjectDocuments());
    }

    private ProjectDto mapToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setProjectCode(project.getProjectCode());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setClient(project.getClient());
        dto.setProjectManager(project.getProjectManager());
        dto.setLinkedQuotation(project.getLinkedQuotation());
        dto.setStartDate(project.getStartDate());
        dto.setExpectedEndDate(project.getExpectedEndDate());
        dto.setExpectedRevenue(project.getExpectedRevenue());
        dto.setBudget(project.getBudget());
        dto.setAssignedVendors(project.getAssignedVendors() != null ? new java.util.ArrayList<>(project.getAssignedVendors()) : new java.util.ArrayList<>());
        dto.setAssignedEntities(project.getAssignedEntities() != null ? new java.util.ArrayList<>(project.getAssignedEntities()) : new java.util.ArrayList<>());
        dto.setLinkedIncidents(project.getLinkedIncidents() != null ? new java.util.ArrayList<>(project.getLinkedIncidents()) : new java.util.ArrayList<>());
        dto.setLinkedQuotations(project.getLinkedQuotations() != null ? new java.util.ArrayList<>(project.getLinkedQuotations()) : new java.util.ArrayList<>());
        dto.setLinkedPOs(project.getLinkedPOs() != null ? new java.util.ArrayList<>(project.getLinkedPOs()) : new java.util.ArrayList<>());
        dto.setLinkedExpenses(project.getLinkedExpenses() != null ? new java.util.ArrayList<>(project.getLinkedExpenses()) : new java.util.ArrayList<>());
        dto.setProjectNotes(project.getProjectNotes() != null ? new java.util.ArrayList<>(project.getProjectNotes()) : new java.util.ArrayList<>());
        dto.setProjectDocuments(project.getProjectDocuments() != null ? new java.util.ArrayList<>(project.getProjectDocuments()) : new java.util.ArrayList<>());
        return dto;
    }
}
