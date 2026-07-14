package com.grivetyglobals.invoiceiq.config;

import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.UserRole;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.grivetyglobals.invoiceiq.entity.project.Project;
import com.grivetyglobals.invoiceiq.entity.sales.Vendor;
import com.grivetyglobals.invoiceiq.entity.finance.PurchaseOrder;
import com.grivetyglobals.invoiceiq.entity.finance.Expense;
import com.grivetyglobals.invoiceiq.entity.finance.Challan;
import com.grivetyglobals.invoiceiq.repository.project.ProjectRepository;
import com.grivetyglobals.invoiceiq.repository.sales.VendorRepository;
import com.grivetyglobals.invoiceiq.repository.finance.PurchaseOrderRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ExpenseRepository;
import com.grivetyglobals.invoiceiq.repository.finance.ChallanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DummyDataSeeder implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ExpenseRepository expenseRepository;
    private final ChallanRepository challanRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (organizationRepository.count() > 0) {
            log.info("Organizations exist. Checking if finance data needs seeding...");
            if (projectRepository.count() == 0 && companyRepository.count() > 0) {
                Company company1 = companyRepository.findAll().get(0);
                seedFinanceData(company1);
            }
            return;
        }

        log.info("Running Development Dummy Data Seeder...");

        // 1. Create Organization
        Organization org = Organization.builder()
                .organizationCode("DUMMYORG")
                .organizationName("Acme Global Corp")
                .organizationEmail("admin@acmeglobal.com")
                .organizationPassword(passwordEncoder.encode("password123"))
                .legalName("Acme Global Corporation Pvt Ltd")
                .organizationType("Private")
                .status("ACTIVE")
                .build();
        org = organizationRepository.save(org);

        Role orgAdminRole = roleRepository.findByRoleName("ROLE_ORG_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_ORG_ADMIN")
                        .systemRole(true)
                        .status("ACTIVE")
                        .build()));

        User orgAdmin = User.builder()
                .username("acme_admin")
                .email("admin@acmeglobal.com")
                .password(passwordEncoder.encode("password123"))
                .emailVerified(true)
                .status("ACTIVE")
                .organization(org)
                .build();
        orgAdmin.getUserRoles().add(UserRole.builder().user(orgAdmin).role(orgAdminRole).build());
        userRepository.save(orgAdmin);

        // 2. Create Company 1
        Company company1 = Company.builder()
                .organization(org)
                .companyCode("ACME-NA")
                .companyName("Acme North America")
                .legalName("Acme NA LLC")
                .email("na_admin@acmeglobal.com")
                .status("ACTIVE")
                .build();
        company1 = companyRepository.save(company1);

        Role companyAdminRole = roleRepository.findByRoleName("ROLE_COMPANY_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_COMPANY_ADMIN")
                        .systemRole(true)
                        .status("ACTIVE")
                        .build()));

        User company1Admin = User.builder()
                .username("na_admin")
                .email("na_admin@acmeglobal.com")
                .password(passwordEncoder.encode("password123"))
                .emailVerified(true)
                .status("ACTIVE")
                .organization(org)
                .company(company1)
                .build();
        company1Admin.getUserRoles().add(UserRole.builder().user(company1Admin).role(companyAdminRole).build());
        userRepository.save(company1Admin);

        // 3. Create Company 2
        Company company2 = Company.builder()
                .organization(org)
                .companyCode("ACME-EU")
                .companyName("Acme Europe")
                .legalName("Acme Europe GmbH")
                .email("eu_admin@acmeglobal.com")
                .status("ACTIVE")
                .build();
        company2 = companyRepository.save(company2);

        User company2Admin = User.builder()
                .username("eu_admin")
                .email("eu_admin@acmeglobal.com")
                .password(passwordEncoder.encode("password123"))
                .emailVerified(true)
                .status("ACTIVE")
                .organization(org)
                .company(company2)
                .build();
        company2Admin.getUserRoles().add(UserRole.builder().user(company2Admin).role(companyAdminRole).build());
        userRepository.save(company2Admin);

        seedFinanceData(company1);

        log.info("Dummy data seeding complete!");
    }

    private void seedFinanceData(Company company) {
        log.info("Seeding Finance Data...");
        
        // 4. Create Vendor
        Vendor vendor = new Vendor();
        vendor.setCompany(company);
        vendor.setVendorNo("VEND-001");
        vendor.setCompanyName("Tech Solutions Inc");
        vendor.setPrimaryContactPerson("John Doe");
        vendor.setEmail("john@techsolutions.com");
        vendor.setPhone("1234567890");
        vendor.setStatus("ACTIVE");
        vendor = vendorRepository.save(vendor);

        // 5. Create Project
        Project project = new Project();
        project.setCompany(company);
        project.setProjectCode("PRJ-001");
        project.setProjectName("Website Redesign");
        project.setDescription("Redesigning the corporate website");
        project.setStatus("IN_PROGRESS");
        project = projectRepository.save(project);

        // 6. Create Purchase Order
        PurchaseOrder po = new PurchaseOrder();
        po.setCompany(company);
        po.setVendor(vendor);
        po.setProject(project);
        po.setPoNumber("PO-2025-001");
        po.setPoDate(LocalDate.now());
        po.setAmount(new BigDecimal("15000.00"));
        po.setRemarks("Initial setup fee");
        po.setStatus("Approved");
        purchaseOrderRepository.save(po);

        // 7. Create Expense
        Expense expense = new Expense();
        expense.setCompany(company);
        expense.setProject(project);
        expense.setExpenseType("Software License");
        expense.setExpenseDate(LocalDate.now());
        expense.setAmount(new BigDecimal("500.00"));
        expense.setRemarks("Figma license");
        expense.setStatus("Approved");
        expenseRepository.save(expense);

        // 8. Create Challan
        Challan challan = new Challan();
        challan.setCompany(company);
        challan.setVendor(vendor);
        challan.setProject(project);
        challan.setChallanNumber("CH-1001");
        challan.setChallanDate(LocalDate.now());
        challan.setRemarks("Delivered 2 laptops");
        challan.setStatus("Received");
        challanRepository.save(challan);
    }
}
