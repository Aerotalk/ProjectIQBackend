package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.CompanyAddressDto;
import com.grivetyglobals.invoiceiq.dto.CompanyBankAccountDto;
import com.grivetyglobals.invoiceiq.dto.CompanyCreateRequest;
import com.grivetyglobals.invoiceiq.dto.CompanyUpdateRequest;
import com.grivetyglobals.invoiceiq.dto.OrganizationCreateRequest;
import com.grivetyglobals.invoiceiq.dto.UserCreateRequest;
import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final CompanyAddressRepository companyAddressRepository;
    private final CompanyBankAccountRepository companyBankAccountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Organization createOrganization(OrganizationCreateRequest request) {
        if (organizationRepository.existsByOrganizationCode(request.getOrganizationCode())) {
            throw new RuntimeException("Organization with code " + request.getOrganizationCode() + " already exists.");
        }
        Organization org = Organization.builder()
                .organizationCode(request.getOrganizationCode())
                .organizationName(request.getOrganizationName())
                .organizationEmail(request.getOrganizationEmail())
                .organizationPassword(passwordEncoder.encode(request.getOrganizationPassword()))
                .legalName(request.getLegalName())
                .organizationType(request.getOrganizationType())
                .industry(request.getIndustry())
                .status(request.getStatus())
                .build();
                
        org = organizationRepository.save(org);

        // Auto-create Organization Admin User so they can login
        if (userRepository.findByEmail(request.getOrganizationEmail()).isEmpty()) {
            User orgAdmin = User.builder()
                    .username(request.getOrganizationName().replaceAll("\\s+", "").toLowerCase() + "_admin")
                    .email(request.getOrganizationEmail())
                    .password(passwordEncoder.encode(request.getOrganizationPassword()))
                    .emailVerified(true)
                    .status("ACTIVE")
                    .organization(org)
                    .build();

            Role orgAdminRole = roleRepository.findByRoleName("ROLE_ORG_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .roleName("ROLE_ORG_ADMIN")
                            .systemRole(true)
                            .description("Organization Administrator")
                            .status("ACTIVE")
                            .build()));

            UserRole orgAdminUserRole = UserRole.builder()
                    .user(orgAdmin)
                    .role(orgAdminRole)
                    .build();
            orgAdmin.getUserRoles().add(orgAdminUserRole);

            userRepository.save(orgAdmin);
        }

        return org;
    }

    public org.springframework.data.domain.Page<Organization> getAllOrganizations(org.springframework.data.domain.Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    public Organization getOrganizationById(java.util.UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional
    public Organization updateOrganization(java.util.UUID id, com.grivetyglobals.invoiceiq.dto.OrganizationUpdateRequest request) {
        Organization org = getOrganizationById(id);
        org.setOrganizationName(request.getOrganizationName());
        org.setOrganizationEmail(request.getOrganizationEmail());
        if (request.getOrganizationPassword() != null && !request.getOrganizationPassword().isEmpty()) {
            org.setOrganizationPassword(request.getOrganizationPassword());
        }
        org.setLegalName(request.getLegalName());
        org.setOrganizationType(request.getOrganizationType());
        org.setIndustry(request.getIndustry());
        org.setStatus(request.getStatus());
        return organizationRepository.save(org);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "companiesList", allEntries = true)
    @Transactional
    public Company createCompany(CompanyCreateRequest request) {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Company company = Company.builder()
                .organization(organization)
                .companyCode(request.getCompanyCode())
                .companyName(request.getCompanyName())
                .legalName(request.getLegalName())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .tanNumber(request.getTanNumber())
                .cinNumber(request.getCinNumber())
                .msmeNumber(request.getMsmeNumber())
                .iecCode(request.getIecCode())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .primaryColor(request.getPrimaryColor())
                .secondaryColor(request.getSecondaryColor())
                .status(request.getStatus())
                .build();

        company = companyRepository.save(company);

        // Auto-create Company Admin User so they can login
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            String rawPassword = (request.getAdminPassword() != null && !request.getAdminPassword().isBlank())
                    ? request.getAdminPassword()
                    : java.util.UUID.randomUUID().toString(); // Fallback random if not provided

            User companyAdmin = User.builder()
                    .username(request.getCompanyName().replaceAll("\\s+", "").toLowerCase() + "_admin")
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(rawPassword))
                    .emailVerified(true)
                    .status("ACTIVE")
                    .organization(organization)
                    .company(company)
                    .build();

            Role companyAdminRole = roleRepository.findByRoleName("ROLE_COMPANY_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .roleName("ROLE_COMPANY_ADMIN")
                            .systemRole(true)
                            .description("Company Administrator")
                            .status("ACTIVE")
                            .build()));
            companyAdmin.getUserRoles().add(UserRole.builder().user(companyAdmin).role(companyAdminRole).build());

            Role employeeRole = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .roleName("ROLE_EMPLOYEE")
                            .systemRole(true)
                            .description("Employee")
                            .status("ACTIVE")
                            .build()));
            companyAdmin.getUserRoles().add(UserRole.builder().user(companyAdmin).role(employeeRole).build());

            userRepository.save(companyAdmin);
        }

        java.util.List<CompanyAddress> savedAddresses = new java.util.ArrayList<>();
        if (request.getAddresses() != null) {
            for (CompanyAddressDto addressDto : request.getAddresses()) {
                CompanyAddress address = CompanyAddress.builder()
                        .company(company)
                        .addressType(addressDto.getAddressType())
                        .addressLine1(addressDto.getAddressLine1())
                        .addressLine2(addressDto.getAddressLine2())
                        .city(addressDto.getCity())
                        .state(addressDto.getState())
                        .country(addressDto.getCountry())
                        .postalCode(addressDto.getPostalCode())
                        .build();
                savedAddresses.add(companyAddressRepository.save(address));
            }
        }
        company.setAddresses(savedAddresses);

        java.util.List<CompanyBankAccount> savedBanks = new java.util.ArrayList<>();
        if (request.getBankAccounts() != null) {
            for (CompanyBankAccountDto bankDto : request.getBankAccounts()) {
                CompanyBankAccount bank = CompanyBankAccount.builder()
                        .company(company)
                        .bankName(bankDto.getBankName())
                        .accountHolderName(bankDto.getAccountHolderName())
                        .accountNumber(bankDto.getAccountNumber())
                        .ifscCode(bankDto.getIfscCode())
                        .swiftCode(bankDto.getSwiftCode())
                        .upiId(bankDto.getUpiId())
                        .isPrimary(bankDto.getIsPrimary())
                        .build();
                savedBanks.add(companyBankAccountRepository.save(bank));
            }
        }
        company.setBankAccounts(savedBanks);

        return company;
    }

    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        java.util.UUID currentOrgId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        java.util.UUID currentCompanyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        
        Organization organization = null;
        if (currentOrgId != null) {
            organization = organizationRepository.findById(currentOrgId).orElseThrow(() -> new RuntimeException("Org not found"));
        } else if (request.getOrganizationId() != null) {
            organization = organizationRepository.findById(request.getOrganizationId()).orElseThrow(() -> new RuntimeException("Org not found"));
        }

        Company company = null;
        if (currentCompanyId != null) {
            company = companyRepository.findById(currentCompanyId).orElseThrow(() -> new RuntimeException("Company not found"));
        } else if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            if (organization != null && !company.getOrganization().getId().equals(organization.getId())) {
                throw new RuntimeException("Access Denied: Company belongs to another organization");
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true)
                .organization(organization)
                .company(company)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .mobile(request.getMobile())
                .build();

        if (request.getRoleIds() != null) {
            for (UUID roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
                com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                user.getUserRoles().add(userRole);
            }
        }
        return userRepository.save(user);
    }

    public org.springframework.data.domain.Page<User> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        java.util.UUID orgId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        java.util.UUID companyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        
        if (companyId != null) {
            return userRepository.findByCompanyId(companyId, pageable);
        } else if (orgId != null) {
            return userRepository.findByOrganizationId(orgId, pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User updateUser(java.util.UUID userId, com.grivetyglobals.invoiceiq.dto.UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        java.util.UUID currentOrgId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        java.util.UUID currentCompanyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        
        if (currentCompanyId != null && (user.getCompany() == null || !user.getCompany().getId().equals(currentCompanyId))) {
            throw new RuntimeException("Access Denied");
        }
        if (currentOrgId != null && (user.getOrganization() == null || !user.getOrganization().getId().equals(currentOrgId))) {
            throw new RuntimeException("Access Denied");
        }
        
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getRoleIds() != null) {
            user.getUserRoles().clear();
            for (UUID roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
                com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                user.getUserRoles().add(userRole);
            }
        }
        
        return userRepository.save(user);
    }
    @org.springframework.cache.annotation.Cacheable(value = "companiesList", key = "T(java.util.Objects).hash(#optionalOrgId, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Company> getAllCompanies(java.util.UUID optionalOrgId, org.springframework.data.domain.Pageable pageable) {
        java.util.UUID organizationId = optionalOrgId != null ? optionalOrgId : com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        if (organizationId != null) {
            return companyRepository.findByOrganizationId(organizationId, pageable);
        }
        return companyRepository.findAll(pageable);
    }

    public Company getCompanyById(java.util.UUID companyId, java.util.UUID optionalOrgId) {
        java.util.UUID organizationId = optionalOrgId != null ? optionalOrgId : com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        if (organizationId != null && !company.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access Denied: Company belongs to another organization");
        }
        
        return company;
    }

    public Company getMyCompanyProfile(String email) {
        java.util.UUID companyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        if (companyId == null) {
            return null; // System Super Admins or Org Admins might not have a specific company
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    @Transactional
    public Company updateCompany(java.util.UUID companyId, CompanyUpdateRequest request, java.util.UUID optionalOrgId) {
        Company company = getCompanyById(companyId, optionalOrgId);

        company.setCompanyName(request.getCompanyName());
        company.setLegalName(request.getLegalName());
        company.setGstNumber(request.getGstNumber());
        company.setPanNumber(request.getPanNumber());
        company.setTanNumber(request.getTanNumber());
        company.setCinNumber(request.getCinNumber());
        company.setMsmeNumber(request.getMsmeNumber());
        company.setIecCode(request.getIecCode());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setWebsite(request.getWebsite());
        company.setLogoFileId(request.getLogoFileId());
        company.setInvoiceLogoId(request.getInvoiceLogoId());
        company.setStampFileId(request.getStampFileId());
        company.setPrimaryColor(request.getPrimaryColor());
        company.setSecondaryColor(request.getSecondaryColor());

        company = companyRepository.save(company);

        // Update admin password if provided
        if (request.getAdminPassword() != null && !request.getAdminPassword().isBlank()) {
            userRepository.findFirstByCompanyIdAndUserRoles_Role_RoleName(company.getId(), "ROLE_COMPANY_ADMIN")
                    .ifPresent(adminUser -> {
                        adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
                        userRepository.save(adminUser);
                    });
        }

        // Update Addresses (Simplistic approach: delete old and recreate new)
        companyAddressRepository.deleteAll(company.getAddresses());
        company.getAddresses().clear();
        
        if (request.getAddresses() != null) {
            for (CompanyAddressDto addressDto : request.getAddresses()) {
                CompanyAddress address = CompanyAddress.builder()
                        .company(company)
                        .addressType(addressDto.getAddressType())
                        .addressLine1(addressDto.getAddressLine1())
                        .addressLine2(addressDto.getAddressLine2())
                        .city(addressDto.getCity())
                        .state(addressDto.getState())
                        .country(addressDto.getCountry())
                        .postalCode(addressDto.getPostalCode())
                        .build();
                company.getAddresses().add(companyAddressRepository.save(address));
            }
        }

        // Update Bank Accounts (Simplistic approach: delete old and recreate new)
        companyBankAccountRepository.deleteAll(company.getBankAccounts());
        company.getBankAccounts().clear();

        if (request.getBankAccounts() != null) {
            for (CompanyBankAccountDto bankDto : request.getBankAccounts()) {
                CompanyBankAccount bank = CompanyBankAccount.builder()
                        .company(company)
                        .bankName(bankDto.getBankName())
                        .accountHolderName(bankDto.getAccountHolderName())
                        .accountNumber(bankDto.getAccountNumber())
                        .ifscCode(bankDto.getIfscCode())
                        .swiftCode(bankDto.getSwiftCode())
                        .upiId(bankDto.getUpiId())
                        .isPrimary(bankDto.getIsPrimary())
                        .build();
                company.getBankAccounts().add(companyBankAccountRepository.save(bank));
            }
        }

        return company;
    }

    @Transactional
    public void deleteCompany(java.util.UUID companyId, java.util.UUID optionalOrgId) {
        Company company = getCompanyById(companyId, optionalOrgId);
        companyRepository.delete(company);
    }

    @Transactional
    public Company updateCompanyStatus(java.util.UUID companyId, String status, java.util.UUID optionalOrgId) {
        Company company = getCompanyById(companyId, optionalOrgId);
        company.setStatus(status);
        return companyRepository.save(company);
    }
    @Transactional(readOnly = true)
    public java.util.List<PermissionGroup> getMyPermissionGroups() {
        User user = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        
        java.util.Set<PermissionGroup> allowedGroups = new java.util.HashSet<>();
        for (com.grivetyglobals.invoiceiq.entity.UserRole ur : user.getUserRoles()) {
            for (com.grivetyglobals.invoiceiq.entity.RolePermissionGroup rpg : ur.getRole().getRolePermissionGroups()) {
                allowedGroups.add(rpg.getPermissionGroup());
            }
        }
        return new java.util.ArrayList<>(allowedGroups);
    }

    @Transactional
    public Role createRole(com.grivetyglobals.invoiceiq.dto.RoleCreateRequest request) {
        java.util.UUID orgId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        java.util.UUID companyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        
        Organization org = null;
        if (orgId != null) {
            org = organizationRepository.findById(orgId).orElseThrow(() -> new RuntimeException("Org not found"));
        }
        
        Company company = null;
        if (companyId != null) {
            company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
        } else if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            if (orgId != null && !company.getOrganization().getId().equals(orgId)) {
                throw new RuntimeException("Access Denied: Company belongs to another organization");
            }
        }
        
        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .organization(org)
                .company(company)
                .systemRole(false)
                .status("ACTIVE")
                .build();
                
        return roleRepository.save(role);
    }

    public java.util.List<Role> getAvailableRoles() {
        java.util.UUID orgId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentOrganizationId();
        java.util.UUID companyId = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentCompanyId();
        
        if (orgId == null && companyId == null) {
            return roleRepository.findAll();
        }
        return roleRepository.findAvailableRoles(orgId, companyId);
    }

    @Transactional
    public Role assignPermissionsToRole(java.util.UUID roleId, com.grivetyglobals.invoiceiq.dto.RolePermissionAssignRequest request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        
        java.util.List<PermissionGroup> myGroups = getMyPermissionGroups();
        java.util.Set<UUID> myGroupIds = new java.util.HashSet<>();
        for (PermissionGroup pg : myGroups) {
            myGroupIds.add(pg.getId());
        }
        
        role.getRolePermissionGroups().clear();
        
        for (UUID pgId : request.getPermissionGroupIds()) {
            if (!myGroupIds.contains(pgId)) {
                throw new RuntimeException("Access Denied: You cannot assign a permission group that you do not possess (ID: " + pgId + ")");
            }
            PermissionGroup pg = new PermissionGroup();
            pg.setId(pgId);
            
            com.grivetyglobals.invoiceiq.entity.RolePermissionGroup rpg = com.grivetyglobals.invoiceiq.entity.RolePermissionGroup.builder()
                    .role(role)
                    .permissionGroup(pg)
                    // Defaults to global, ideally should be configurable but for now set to default
                    .dataScope(com.grivetyglobals.invoiceiq.enums.DataScope.GLOBAL) 
                    .build();
            role.getRolePermissionGroups().add(rpg);
        }
        
        return roleRepository.save(role);
    }
}
