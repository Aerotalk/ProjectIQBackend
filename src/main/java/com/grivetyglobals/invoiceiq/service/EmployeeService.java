package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.EmployeeCreateRequest;
import com.grivetyglobals.invoiceiq.dto.EmployeeUpdateRequest;
import com.grivetyglobals.invoiceiq.entity.Department;
import com.grivetyglobals.invoiceiq.entity.Designation;
import com.grivetyglobals.invoiceiq.entity.Employee;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.repository.DepartmentRepository;
import com.grivetyglobals.invoiceiq.repository.DesignationRepository;
import com.grivetyglobals.invoiceiq.repository.EmployeeRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing employees.
 * Provides functionality for creating, retrieving, updating, deleting, and filtering employees.
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    private String generateEmployeeCode(UUID organizationId) {
        long currentCount = employeeRepository.countByOrganizationId(organizationId);
        return String.format("EMP-%04d", currentCount + 1);
    }

    /**
     * Creates a new employee.
     * 
     * @param request the employee creation payload
     * @return the created Employee entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "employeesList", allEntries = true)
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

        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();
        Company company = null;
        if (currentCompanyId != null) {
            company = companyRepository.findById(currentCompanyId).orElse(null);
        }

        Employee hrManager = null;
        if (request.getHrManagerId() != null) {
            hrManager = employeeRepository.findById(request.getHrManagerId())
                    .orElse(null); // non-mandatory
        }

        Employee employee = Employee.builder()
                .organization(organization)
                .company(company)
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
                .maritalStatus(request.getMaritalStatus())
                .bloodGroup(request.getBloodGroup())
                .nationality(request.getNationality())
                .employmentType(request.getEmploymentType())
                .location(request.getLocation())
                .grade(request.getGrade())
                .hrManager(hrManager)
                .weeklyOff(request.getWeeklyOff())
                .fatherName(request.getFatherName())
                .noticePeriodDays(request.getNoticePeriodDays())
                .alternatePhone(request.getAlternatePhone())
                .build();

        return employeeRepository.save(employee);
    }

    /**
     * Retrieves an employee by their UUID.
     * Ensures the employee belongs to the current organization and company (if applicable).
     * 
     * @param employeeId the UUID of the employee
     * @return the Employee entity
     * @throws RuntimeException if the employee is not found or access is denied
     */
    public Employee getEmployeeById(UUID employeeId) {
        UUID currentOrgId = SecurityUtils.getCurrentOrganizationId();
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyId();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!employee.getOrganization().getId().equals(currentOrgId)) {
            throw new RuntimeException("Access Denied: Employee belongs to another organization");
        }

        // Data scope check: if the user belongs to a specific company, they can only
        // view employees of that company
        if (currentCompanyId != null && employee.getCompany() != null
                && !employee.getCompany().getId().equals(currentCompanyId)) {
            throw new RuntimeException("Access Denied: Employee belongs to another company");
        }

        return employee;
    }

    /**
     * Searches and filters employees based on department, status, and search term.
     * 
     * @param departmentId the optional department ID filter
     * @param status       the optional employment status filter
     * @param searchTerm   the optional search term (matches name or code)
     * @return a list of Employee entities
     */
    @org.springframework.cache.annotation.Cacheable(value = "employeesList", key = "T(java.util.Objects).hash(T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentOrganizationId(), T(com.grivetyglobals.invoiceiq.security.SecurityUtils).getCurrentCompanyId(), #departmentId, #status, #searchTerm, #roleName)")
    @Transactional(readOnly = true)
    public List<Employee> searchAndFilterEmployees(UUID departmentId, String status, String searchTerm, String roleName) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        UUID companyId = SecurityUtils.getCurrentCompanyId();
        return employeeRepository.searchAndFilterEmployees(organizationId, companyId, departmentId, status, searchTerm, roleName);
    }

    /**
     * Retrieves the employee profile for a specific user email.
     * 
     * @param email the email of the user
     * @return the associated Employee entity
     */
    public Employee getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
    }

    /**
     * Updates an existing employee.
     * 
     * @param employeeId the UUID of the employee to update
     * @param request    the update payload
     * @return the updated Employee entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "employeesList", allEntries = true)
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

        Employee hrManager = null;
        if (request.getHrManagerId() != null) {
            hrManager = employeeRepository.findById(request.getHrManagerId())
                    .orElse(null);
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

        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setBloodGroup(request.getBloodGroup());
        employee.setNationality(request.getNationality());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setLocation(request.getLocation());
        employee.setGrade(request.getGrade());
        employee.setHrManager(hrManager);
        employee.setWeeklyOff(request.getWeeklyOff());
        employee.setFatherName(request.getFatherName());
        employee.setNoticePeriodDays(request.getNoticePeriodDays());
        employee.setAlternatePhone(request.getAlternatePhone());

        return employeeRepository.save(employee);
    }

    /**
     * Deletes an employee by their UUID.
     * 
     * @param employeeId the UUID of the employee to delete
     */
    @Transactional
    public void deleteEmployee(UUID employeeId) {
        Employee employee = getEmployeeById(employeeId);
        employeeRepository.delete(employee);
    }

    /**
     * Changes the employment status of an employee.
     * 
     * @param employeeId the UUID of the employee
     * @param status     the new employment status
     * @return the updated Employee entity
     */
    @org.springframework.cache.annotation.CacheEvict(value = "employeesList", allEntries = true)
    @Transactional
    public Employee changeEmploymentStatus(UUID employeeId, String status) {
        Employee employee = getEmployeeById(employeeId);
        employee.setEmploymentStatus(status);
        return employeeRepository.save(employee);
    }
}
