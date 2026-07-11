package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.EmployeeCreateRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeUpdateRequest;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Designation;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.DepartmentRepository;
import com.grivetyglobals.invoiceiq.repository.DesignationRepository;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    private String generateEmployeeCode(UUID organizationId) {
        long currentCount = employeeRepository.countByOrganizationId(organizationId);
        return String.format("EMP-%04d", currentCount + 1);
    }

    @Transactional
    public Employee createEmployee(EmployeeCreateRequest request) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        
        Organization organization = organizationRepository.findById(currentOrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Designation designation = null;
        if (request.getDesignationId() != null) {
            designation = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
        }

        Employee reportingManager = null;
        if (request.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findById(request.getReportingManagerId())
                    .orElseThrow(() -> new RuntimeException("Reporting Manager not found"));
        }

        Employee employee = Employee.builder()
                .organization(organization)
                .user(user)
                .employeeCode(generateEmployeeCode(organization.getId()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .joiningDate(request.getJoiningDate())
                .department(department)
                .designation(designation)
                .reportingManager(reportingManager)
                .profilePicture(request.getProfilePicture())
                .employmentStatus(request.getEmploymentStatus())
                .build();

        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(UUID employeeId) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!employee.getOrganization().getId().equals(currentOrgId)) {
            throw new RuntimeException("Access Denied: Employee belongs to another organization");
        }
        
        // Data scope check: if the user belongs to a specific company, they can only view employees of that company
        if (currentCompanyId != null && employee.getCompany() != null && !employee.getCompany().getId().equals(currentCompanyId)) {
            throw new RuntimeException("Access Denied: Employee belongs to another company");
        }

        return employee;
    }

    public List<Employee> searchAndFilterEmployees(UUID departmentId, String status, String keyword) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
        // Here we pass the currentCompanyId. If it's null (e.g. Org Admin), it fetches for all companies in the Org.
        return employeeRepository.searchAndFilterEmployees(currentOrgId, currentCompanyId, departmentId, status, keyword);
    }

    public Employee getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
    }

    @Transactional
    public Employee updateEmployee(UUID employeeId, EmployeeUpdateRequest request) {
        Employee employee = getEmployeeById(employeeId);

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Designation designation = null;
        if (request.getDesignationId() != null) {
            designation = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
        }

        Employee reportingManager = null;
        if (request.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findById(request.getReportingManagerId())
                    .orElseThrow(() -> new RuntimeException("Reporting Manager not found"));
        }

        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setReportingManager(reportingManager);
        employee.setProfilePicture(request.getProfilePicture());

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(UUID employeeId) {
        Employee employee = getEmployeeById(employeeId);
        employeeRepository.delete(employee);
    }

    @Transactional
    public Employee changeEmploymentStatus(UUID employeeId, String status) {
        Employee employee = getEmployeeById(employeeId);
        employee.setEmploymentStatus(status);
        return employeeRepository.save(employee);
    }
}
