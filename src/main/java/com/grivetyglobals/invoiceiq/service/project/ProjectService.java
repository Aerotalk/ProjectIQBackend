package com.grivetyglobals.invoiceiq.service.project;

import com.grivetyglobals.invoiceiq.dto.finance.PurchaseOrderDto;
import com.grivetyglobals.invoiceiq.dto.project.ProjectDto;
import com.grivetyglobals.invoiceiq.dto.sales.QuotationDto;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder;
import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Quotation;
import com.grivetyglobals.invoiceiq.exception.ResourceNotFoundException;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.finance.PurchaseOrderRepository;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import com.grivetyglobals.invoiceiq.repository.sales.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

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

    // -------------------------------------------------------------------------
    // Transactions & Docs: Link / Unlink Quotations
    // -------------------------------------------------------------------------

    /**
     * Returns all quotations for the given company, used to populate the
     * "link existing quotation" picker on the project dashboard.
     */
    @Transactional(readOnly = true)
    public List<QuotationDto> getAvailableQuotations(UUID companyId) {
        return quotationRepository.findByCompanyId(companyId).stream()
                .map(this::mapQuotationToDto)
                .collect(Collectors.toList());
    }

    /**
     * Links an existing quotation to a project by adding its ID to the
     * project's linkedQuotations list (stored as strings).
     */
    @Transactional
    public ProjectDto linkQuotation(UUID projectId, UUID quotationId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));

        List<String> linked = project.getLinkedQuotations() != null
                ? new ArrayList<>(project.getLinkedQuotations()) : new ArrayList<>();
        String idStr = quotationId.toString();
        if (!linked.contains(idStr)) {
            linked.add(idStr);
            project.setLinkedQuotations(linked);
        }
        return mapToDto(projectRepository.save(project));
    }

    /**
     * Removes a quotation link from the project.
     */
    @Transactional
    public ProjectDto unlinkQuotation(UUID projectId, UUID quotationId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<String> linked = project.getLinkedQuotations() != null
                ? new ArrayList<>(project.getLinkedQuotations()) : new ArrayList<>();
        linked.remove(quotationId.toString());
        project.setLinkedQuotations(linked);
        return mapToDto(projectRepository.save(project));
    }

    // -------------------------------------------------------------------------
    // Transactions & Docs: Link / Unlink Purchase Orders
    // -------------------------------------------------------------------------

    /**
     * Returns all purchase orders for the given company, used to populate the
     * "link existing PO" picker on the project dashboard.
     */
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAvailablePOs(UUID companyId) {
        return purchaseOrderRepository.findByCompanyId(companyId).stream()
                .map(this::mapPOToDto)
                .collect(Collectors.toList());
    }

    /**
     * Links an existing purchase order to a project by adding its ID to the
     * project's linkedPOs list (stored as strings).
     */
    @Transactional
    public ProjectDto linkPO(UUID projectId, UUID poId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));

        List<String> linked = project.getLinkedPOs() != null
                ? new ArrayList<>(project.getLinkedPOs()) : new ArrayList<>();
        String idStr = poId.toString();
        if (!linked.contains(idStr)) {
            linked.add(idStr);
            project.setLinkedPOs(linked);
        }
        return mapToDto(projectRepository.save(project));
    }

    /**
     * Removes a purchase order link from the project.
     */
    @Transactional
    public ProjectDto unlinkPO(UUID projectId, UUID poId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<String> linked = project.getLinkedPOs() != null
                ? new ArrayList<>(project.getLinkedPOs()) : new ArrayList<>();
        linked.remove(poId.toString());
        project.setLinkedPOs(linked);
        return mapToDto(projectRepository.save(project));
    }

    private void mapToEntity(ProjectDto dto, Project project) {
        if (dto.getProjectCode() != null && !dto.getProjectCode().trim().isEmpty()) {
            project.setProjectCode(dto.getProjectCode());
        }
        if (dto.getProjectName() != null && !dto.getProjectName().trim().isEmpty()) {
            project.setProjectName(dto.getProjectName());
        }
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

    // -------------------------------------------------------------------------
    // Private mappers for Quotation & PO summary DTOs
    // -------------------------------------------------------------------------

    private QuotationDto mapQuotationToDto(Quotation q) {
        QuotationDto dto = new QuotationDto();
        dto.setId(q.getId());
        dto.setQuotationNo(q.getQuotationNo());
        dto.setClientName(q.getClientName());
        dto.setDate(q.getDate());
        dto.setGrandTotal(q.getGrandTotal());
        dto.setStatus(q.getStatus());
        dto.setSubject(q.getSubject());
        dto.setCreatedAt(q.getCreatedAt());
        return dto;
    }

    private PurchaseOrderDto mapPOToDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setPoDate(po.getPoDate());
        dto.setGrandTotal(po.getGrandTotal());
        dto.setStatus(po.getStatus());
        dto.setDescription(po.getDescription());
        if (po.getVendor() != null) {
            dto.setVendorId(po.getVendor().getId());
            dto.setVendorName(po.getVendor().getCompanyName());
        }
        dto.setCreatedAt(po.getCreatedAt());
        return dto;
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
