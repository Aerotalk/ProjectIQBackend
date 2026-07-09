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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (organizationRepository.count() > 0) {
            log.info("Dummy data already exists. Skipping dummy seeder.");
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

        log.info("Dummy data seeding complete!");
    }
}
