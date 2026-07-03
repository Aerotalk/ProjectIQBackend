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
    private final AuditService auditService;

    @Transactional
    public Application createApplication(ApplicationRequest request, UUID userId, UUID organizationId) {
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
        auditService.logActivity("APPLICATION_CREATED", "Created application: " + request.getApplicationName(), saved.getId(), "Application", userId, organizationId);
        return saved;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional
    public void assignApplicationsToEmployee(UUID employeeId, List<UUID> applicationIds, UUID userId, UUID organizationId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<Application> applications = applicationRepository.findAllById(applicationIds);
        if (applications.size() != applicationIds.size()) {
            throw new RuntimeException("One or more applications not found");
        }

        employee.setApplications(Set.copyOf(applications));
        employeeRepository.save(employee);
        
        auditService.logActivity("EMPLOYEE_APPLICATIONS_UPDATED", "Updated application access for employee " + employee.getFirstName(), employeeId, "Employee", userId, organizationId);
    }
}
