package com.grivetyglobals.invoiceiq.controller.project;

import com.grivetyglobals.invoiceiq.dto.project.ProjectDto;
import com.grivetyglobals.invoiceiq.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing projects.
 * Provides endpoints for creating, reading, updating, and deleting projects.
 */
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Retrieves all projects for a specific company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @return a list of ProjectDto objects
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjects(@RequestParam UUID companyId) {
        return ResponseEntity.ok(projectService.getProjectsByCompany(companyId));
    }

    /**
     * Retrieves a specific project by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the project
     * @return the ProjectDto object
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    /**
     * Creates a new project for a given company.
     * Requires an authenticated session.
     *
     * @param companyId the UUID of the company
     * @param dto       the project data payload
     * @return the created ProjectDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestParam UUID companyId, @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(projectService.createProject(companyId, dto));
    }

    /**
     * Updates an existing project.
     * Requires an authenticated session.
     *
     * @param id  the UUID of the project to update
     * @param dto the updated project data payload
     * @return the updated ProjectDto object
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID id, @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    /**
     * Deletes a project by its UUID.
     * Requires an authenticated session.
     *
     * @param id the UUID of the project to delete
     * @return a 204 No Content response
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
