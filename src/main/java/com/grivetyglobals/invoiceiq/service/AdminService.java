package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.CompanyAddressDto;
import com.grivetyglobals.invoiceiq.dto.CompanyBankAccountDto;
import com.grivetyglobals.invoiceiq.dto.CompanyCreateRequest;
import com.grivetyglobals.invoiceiq.dto.OrganizationCreateRequest;
import com.grivetyglobals.invoiceiq.dto.UserCreateRequest;
import com.grivetyglobals.invoiceiq.entity.*;
import com.grivetyglobals.invoiceiq.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Organization organization = Organization.builder()
                .organizationCode(request.getOrganizationCode())
                .organizationName(request.getOrganizationName())
                .legalName(request.getLegalName())
                .organizationType(request.getOrganizationType())
                .industry(request.getIndustry())
                .status(request.getStatus())
                .build();
        return organizationRepository.save(organization);
    }

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

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(Role.builder().name(request.getRole()).build()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true)
                .organization(organization)
                .build();

        user.getRoles().add(role);
        return userRepository.save(user);
    }
}
