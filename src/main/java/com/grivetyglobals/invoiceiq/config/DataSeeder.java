package com.grivetyglobals.invoiceiq.config;

import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.UserRole;
import com.grivetyglobals.invoiceiq.repository.OrganizationRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

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

        // 3. Seed Default Organization
        Organization aerotalkOrg = organizationRepository.findByOrganizationCode("ATK")
                .orElseGet(() -> organizationRepository.save(Organization.builder()
                        .organizationCode("ATK")
                        .organizationName("Aerotalk")
                        .organizationEmail("contact@aerotalk.com")
                        .organizationPassword(passwordEncoder.encode("password123"))
                        .legalName("Aerotalk Pvt Ltd")
                        .organizationType("IT Services")
                        .industry("Technology")
                        .status("ACTIVE")
                        .build()));

        // 4. Seed Organization Admin Account
        if (userRepository.findByEmail("admin@aerotalk.com").isEmpty()) {
            User orgAdmin = User.builder()
                    .username("admin")
                    .email("admin@aerotalk.com")
                    .password(passwordEncoder.encode("password123"))
                    .emailVerified(true)
                    .mobile("1111111111")
                    .status("ACTIVE")
                    .organization(aerotalkOrg)
                    .build();

            UserRole orgAdminUserRole = UserRole.builder()
                    .user(orgAdmin)
                    .role(orgAdminRole)
                    .build();
            orgAdmin.getUserRoles().add(orgAdminUserRole);

            userRepository.save(orgAdmin);
            log.info("Organization Admin account seeded: admin@aerotalk.com / password123");
        }

        // 5. Seed Company Admin Account
        if (userRepository.findByEmail("company@aerotalk.in").isEmpty()) {
            User companyAdmin = User.builder()
                    .username("company")
                    .email("company@aerotalk.in")
                    .password(passwordEncoder.encode("password123"))
                    .emailVerified(true)
                    .mobile("2222222222")
                    .status("ACTIVE")
                    .organization(aerotalkOrg) // Just attaching to ATK for test purposes
                    .build();

            UserRole companyAdminUserRole = UserRole.builder()
                    .user(companyAdmin)
                    .role(companyAdminRole)
                    .build();
            companyAdmin.getUserRoles().add(companyAdminUserRole);

            userRepository.save(companyAdmin);
            log.info("Company Admin account seeded: company@aerotalk.in / password123");
        }

        log.info("DataSeeder completed.");
    }
}
