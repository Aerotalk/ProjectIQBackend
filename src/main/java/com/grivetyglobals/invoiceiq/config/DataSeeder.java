package com.grivetyglobals.invoiceiq.config;

import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.UserRole;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.repository.*;
import com.grivetyglobals.invoiceiq.enums.DataScope;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionGroupMappingRepository permissionGroupMappingRepository;
    private final RolePermissionGroupRepository rolePermissionGroupRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Running DataSeeder...");

        // 1. Seed Roles
        Role superAdminRole = roleRepository.findByRoleName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_SUPER_ADMIN")
                        .systemRole(true)
                        .description("System Super Administrator")
                        .status("ACTIVE")
                        .build()));

        Role orgAdminRole = roleRepository.findByRoleName("ROLE_ORG_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_ORG_ADMIN")
                        .systemRole(true)
                        .description("Organization Administrator")
                        .status("ACTIVE")
                        .build()));

        Role companyAdminRole = roleRepository.findByRoleName("ROLE_COMPANY_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_COMPANY_ADMIN")
                        .systemRole(true)
                        .description("Company Administrator")
                        .status("ACTIVE")
                        .build()));

        // 0. Seed Permissions
        seedPermissions(superAdminRole, orgAdminRole, companyAdminRole);

        // 2. Seed Super Admin Account
        if (userRepository.findByEmail("superadmin@aerotalk.in").isEmpty()) {
            User superAdmin = User.builder()
                    .username("superadmin")
                    .email("superadmin@aerotalk.in")
                    .password(passwordEncoder.encode("password123"))
                    .emailVerified(true)
                    .mobile("0000000000")
                    .status("ACTIVE")
                    .build();

            UserRole superAdminUserRole = UserRole.builder()
                    .user(superAdmin)
                    .role(superAdminRole)
                    .build();
            superAdmin.getUserRoles().add(superAdminUserRole);

            userRepository.save(superAdmin);
            log.info("Super Admin account seeded: superadmin@aerotalk.in / password123");
        }



        log.info("DataSeeder completed.");
    }

    private void seedPermissions(Role superAdminRole, Role orgAdminRole, Role companyAdminRole) {
        // Create Permission Groups
        PermissionGroup orgGroup = createPermissionGroup("Organization Management", "Manage organizations and teams");
        PermissionGroup companyGroup = createPermissionGroup("Company Management", "Manage companies");
        PermissionGroup empGroup = createPermissionGroup("Employee Management", "HR and Employee lifecycle");
        PermissionGroup deptGroup = createPermissionGroup("Department Management", "Manage departments");
        PermissionGroup roleGroup = createPermissionGroup("Role Management", "Manage roles and permissions");
        PermissionGroup settingGroup = createPermissionGroup("Settings Management", "Manage system settings");
        
        PermissionGroup erpGroup = createPermissionGroup("ERP Operations", "Customers, Vendors, Products, Quotations");
        PermissionGroup financeGroup = createPermissionGroup("Finance Operations", "Invoices, Payments, Expenses, POs");
        PermissionGroup hrGroup = createPermissionGroup("HR Operations", "Attendance, Leaves, Payroll, Recruitment");
        PermissionGroup incidentGroup = createPermissionGroup("Incident Management", "Tickets, SLAs, Knowledge Base");
        PermissionGroup projectGroup = createPermissionGroup("Project Management", "Projects, Tasks, Milestones");
        PermissionGroup reportGroup = createPermissionGroup("Reports Management", "Analytics & BI");

        // Seed and Map Org Permissions
        mapPermissionToGroup(orgGroup, createPermission("org.view", "View Organizations", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("org.create", "Create Organization", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("org.edit", "Edit Organization", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("org.delete", "Delete Organization", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("team.view", "View Teams", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("team.create", "Create Team", "Organization"));
        mapPermissionToGroup(orgGroup, createPermission("approval.configure", "Configure Approvals", "Organization"));

        // Seed and Map Company Permissions
        mapPermissionToGroup(companyGroup, createPermission("company.view", "View Companies", "Organization"));
        mapPermissionToGroup(companyGroup, createPermission("company.create", "Create Company", "Organization"));
        mapPermissionToGroup(companyGroup, createPermission("company.edit", "Edit Company", "Organization"));
        mapPermissionToGroup(companyGroup, createPermission("company.delete", "Delete Company", "Organization"));

        // Seed and Map Employee Permissions
        mapPermissionToGroup(empGroup, createPermission("employee.view", "View Employees", "HRMS"));
        mapPermissionToGroup(empGroup, createPermission("employee.create", "Create Employee", "HRMS"));
        mapPermissionToGroup(empGroup, createPermission("employee.edit", "Edit Employee", "HRMS"));
        mapPermissionToGroup(empGroup, createPermission("employee.delete", "Delete Employee", "HRMS"));

        // Seed and Map Department Permissions
        mapPermissionToGroup(deptGroup, createPermission("department.view", "View Departments", "Organization"));
        mapPermissionToGroup(deptGroup, createPermission("department.create", "Create Department", "Organization"));
        mapPermissionToGroup(deptGroup, createPermission("department.edit", "Edit Department", "Organization"));
        mapPermissionToGroup(deptGroup, createPermission("department.delete", "Delete Department", "Organization"));

        // Seed and Map Role Permissions
        mapPermissionToGroup(roleGroup, createPermission("role.view", "View Roles", "Organization"));
        mapPermissionToGroup(roleGroup, createPermission("role.create", "Create Role", "Organization"));
        mapPermissionToGroup(roleGroup, createPermission("role.edit", "Edit Role", "Organization"));
        mapPermissionToGroup(roleGroup, createPermission("role.delete", "Delete Role", "Organization"));
        mapPermissionToGroup(roleGroup, createPermission("role.assign", "Assign Role", "Organization"));
        mapPermissionToGroup(roleGroup, createPermission("permission.view", "View Permissions", "Organization"));

        // Seed and Map Setting Permissions
        mapPermissionToGroup(settingGroup, createPermission("setting.view", "View Settings", "Organization"));
        mapPermissionToGroup(settingGroup, createPermission("setting.edit", "Edit Settings", "Organization"));

        // General ERP Permissions
        mapPermissionToGroup(erpGroup, createPermission("dashboard.view", "View Dashboard", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("customer.view", "View Customers", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("customer.create", "Create Customer", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("customer.edit", "Edit Customer", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("customer.delete", "Delete Customer", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("vendor.view", "View Vendors", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("vendor.create", "Create Vendor", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("vendor.edit", "Edit Vendor", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("product.view", "View Products", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("product.create", "Create Product", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("quotation.view", "View Quotations", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("quotation.create", "Create Quotation", "ERP"));
        mapPermissionToGroup(erpGroup, createPermission("quotation.approve", "Approve Quotation", "ERP"));

        // Finance Permissions
        mapPermissionToGroup(financeGroup, createPermission("finance.view", "View Finance Dashboard", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("invoice.view", "View Invoices", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("invoice.generate", "Generate Invoice", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("payment.view", "View Payments", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("payment.create", "Record Payment", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("po.view", "View Purchase Orders", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("po.create", "Create PO", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("expense.view", "View Expenses", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("expense.create", "Create Expense", "Finance"));
        mapPermissionToGroup(financeGroup, createPermission("expense.approve", "Approve Expense", "Finance"));

        // HR Permissions
        mapPermissionToGroup(hrGroup, createPermission("hr.view", "View HR Dashboard", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("attendance.view", "View Attendance", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("attendance.manage", "Manage Attendance", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("leave.view", "View Leaves", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("leave.apply", "Apply Leave", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("leave.approve", "Approve Leave", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("payroll.view", "View Payroll", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("payroll.generate", "Generate Payroll", "HRMS"));
        mapPermissionToGroup(hrGroup, createPermission("recruitment.view", "View Recruitment", "HRMS"));

        // Incident Permissions
        mapPermissionToGroup(incidentGroup, createPermission("incident.view", "View Incidents", "Incident"));
        mapPermissionToGroup(incidentGroup, createPermission("incident.create", "Create Incident", "Incident"));
        mapPermissionToGroup(incidentGroup, createPermission("incident.update", "Update Incident", "Incident"));
        mapPermissionToGroup(incidentGroup, createPermission("incident.escalate", "Escalate Incident", "Incident"));
        mapPermissionToGroup(incidentGroup, createPermission("knowledge.view", "View Knowledge Base", "Incident"));

        // Project Permissions
        mapPermissionToGroup(projectGroup, createPermission("project.view", "View Projects", "Projects"));
        mapPermissionToGroup(projectGroup, createPermission("project.create", "Create Project", "Projects"));
        mapPermissionToGroup(projectGroup, createPermission("project.edit", "Edit Project", "Projects"));
        mapPermissionToGroup(projectGroup, createPermission("project.close", "Close Project", "Projects"));

        // Report Permissions
        mapPermissionToGroup(reportGroup, createPermission("reports.view", "View Reports", "Reports"));
        mapPermissionToGroup(reportGroup, createPermission("reports.edit", "Edit Reports", "Reports"));
        mapPermissionToGroup(reportGroup, createPermission("reports.export", "Export Reports", "Reports"));

        // Assign Groups to Super Admin Role (GLOBAL Scope)
        mapGroupToRole(superAdminRole, orgGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, companyGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, empGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, deptGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, roleGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, settingGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, erpGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, financeGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, hrGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, incidentGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, projectGroup, DataScope.GLOBAL);
        mapGroupToRole(superAdminRole, reportGroup, DataScope.GLOBAL);

        // Assign Groups to Org Admin Role (ORGANIZATION Scope)
        mapGroupToRole(orgAdminRole, orgGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, companyGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, settingGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, empGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, deptGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, roleGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, erpGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, financeGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, hrGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, incidentGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, projectGroup, DataScope.ORGANIZATION);
        mapGroupToRole(orgAdminRole, reportGroup, DataScope.ORGANIZATION);

        // Assign Groups to Company Admin Role (COMPANY Scope)
        mapGroupToRole(companyAdminRole, companyGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, settingGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, empGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, deptGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, roleGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, erpGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, financeGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, hrGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, incidentGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, projectGroup, DataScope.COMPANY);
        mapGroupToRole(companyAdminRole, reportGroup, DataScope.COMPANY);
    }

    private PermissionGroup createPermissionGroup(String name, String description) {
        return permissionGroupRepository.findByGroupName(name)
                .orElseGet(() -> permissionGroupRepository.save(PermissionGroup.builder()
                        .groupName(name)
                        .description(description)
                        .build()));
    }

    private Permission createPermission(String key, String name, String module) {
        return permissionRepository.findByPermissionKey(key)
                .orElseGet(() -> permissionRepository.save(Permission.builder()
                        .permissionKey(key)
                        .permissionName(name)
                        .module(module)
                        .build()));
    }

    private void mapPermissionToGroup(PermissionGroup group, Permission permission) {
        boolean alreadyMapped = group.getPermissions().stream()
                .anyMatch(mapping -> mapping.getPermission().getId().equals(permission.getId()));
        
        if (!alreadyMapped) {
            PermissionGroupMapping mapping = PermissionGroupMapping.builder()
                    .permissionGroup(group)
                    .permission(permission)
                    .build();
            permissionGroupMappingRepository.save(mapping);
            group.getPermissions().add(mapping);
        }
    }

    private void mapGroupToRole(Role role, PermissionGroup group, DataScope scope) {
        boolean alreadyMapped = role.getRolePermissionGroups().stream()
                .anyMatch(rpg -> rpg.getPermissionGroup().getId().equals(group.getId()));

        if (!alreadyMapped) {
            RolePermissionGroup rpg = RolePermissionGroup.builder()
                    .role(role)
                    .permissionGroup(group)
                    .dataScope(scope)
                    .build();
            rolePermissionGroupRepository.save(rpg);
            role.getRolePermissionGroups().add(rpg);
        }
    }
}
