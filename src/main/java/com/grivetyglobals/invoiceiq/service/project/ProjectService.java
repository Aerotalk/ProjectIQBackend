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

    public List<ProjectDto> getProjectsByCompany(UUID companyId) {
        return projectRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProject(UUID id) {
        return mapToDto(projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found")));
    }

    @Transactional
    public ProjectDto createProject(UUID companyId, ProjectDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

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
    }

    private ProjectDto mapToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setProjectCode(project.getProjectCode());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        return dto;
    }
}
