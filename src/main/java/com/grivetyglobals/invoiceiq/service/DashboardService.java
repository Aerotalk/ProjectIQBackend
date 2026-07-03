package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.DashboardMetricsResponse;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.DepartmentRepository;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

    public DashboardMetricsResponse getDashboardMetrics(UUID organizationId) {
        
        long totalCompanies = companyRepository.countByOrganizationId(organizationId);
        long totalEmployees = employeeRepository.countByOrganizationId(organizationId);
        long totalDepartments = departmentRepository.countByOrganizationId(organizationId);
        long totalRoles = roleRepository.count(); // Roles are global across the application

        return DashboardMetricsResponse.builder()
                .totalCompanies(totalCompanies)
                .totalEmployees(totalEmployees)
                .totalDepartments(totalDepartments)
                .totalRoles(totalRoles)
                .totalApplications(0) // Mocked for now
                .recentActivityLogs(new ArrayList<>()) // Mocked for now
                .systemNotice("Note: 'Applications' and 'Activity Logs' are currently mocked as they are pending implementation.")
                .build();
    }
}
