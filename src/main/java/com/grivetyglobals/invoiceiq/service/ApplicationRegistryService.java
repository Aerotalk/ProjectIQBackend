package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.ApplicationRequest;
import com.grivetyglobals.invoiceiq.entity.Application;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.repository.ApplicationRepository;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationRegistryService {

    private final ApplicationRepository applicationRepository;
    private final EmployeeRepository employeeRepository;
    private final com.grivetyglobals.invoiceiq.repository.CompanyRepository companyRepository;
    private final AuditService auditService;

    @Transactional
    public Application createApplication(ApplicationRequest request) {
        if (applicationRepository.existsByApplicationName(request.getApplicationName())) {
            throw new RuntimeException("Application with name " + request.getApplicationName() + " already exists.");
        }
        Application app = Application.builder()
                .applicationCode(request.getApplicationCode())
                .applicationName(request.getApplicationName())
                .applicationRoute(request.getApplicationRoute())
                .icon(request.getIcon())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();
        Application saved = applicationRepository.save(app);
        
        UUID userId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser().getId();
        UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        auditService.logActivity("APPLICATION_CREATED", "Created application: " + request.getApplicationName(), saved.getId(), "Application", userId, organizationId);
        return saved;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional
    public void assignApplicationsToEmployee(UUID employeeId, List<UUID> applicationIds) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        com.grivetyglobals.invoiceiq.entity.User user = employee.getUser();
        if (user == null) {
            throw new RuntimeException("Employee does not have an associated user account");
        }

        List<Application> applications = applicationRepository.findAllById(applicationIds);
        if (applications.size() != applicationIds.size()) {
            throw new RuntimeException("One or more applications not found");
        }

        user.getUserApplications().clear();
        for (Application app : applications) {
            user.getUserApplications().add(com.grivetyglobals.invoiceiq.entity.UserApplication.builder()
                    .user(user)
                    .application(app)
                    .isEnabled(true)
                    .build());
        }
        
        UUID userId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser().getId();
        UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        auditService.logActivity("EMPLOYEE_APPLICATIONS_UPDATED", "Updated application access for employee " + employee.getFirstName(), employeeId, "Employee", userId, organizationId);
    }

    @Transactional
    public void assignApplicationsToCompany(UUID companyId, List<UUID> applicationIds) {
        UUID organizationId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        com.grivetyglobals.invoiceiq.entity.Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        if (!company.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Company does not belong to the specified organization");
        }

        List<Application> applications = applicationRepository.findAllById(applicationIds);
        if (applications.size() != applicationIds.size()) {
            throw new RuntimeException("One or more applications not found");
        }

        company.getCompanyApplications().clear();
        for (Application app : applications) {
            company.getCompanyApplications().add(com.grivetyglobals.invoiceiq.entity.CompanyApplication.builder()
                    .company(company)
                    .application(app)
                    .status("Active")
                    .build());
        }

        UUID userId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser().getId();
        auditService.logActivity("COMPANY_APPLICATIONS_UPDATED", "Updated module assignments for company " + company.getCompanyName(), companyId, "Company", userId, organizationId);
    }
}
