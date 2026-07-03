package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.CompanyCreateRequest;
import com.grivetyglobals.invoiceiq.dto.UserCreateRequest;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Company createCompany(CompanyCreateRequest request) {
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .country(request.getCountry())
                .state(request.getState())
                .district(request.getDistrict())
                .pincode(request.getPincode())
                .gst(request.getGst())
                .address(request.getAddress())
                .companyPhoneNo(request.getCompanyPhoneNo())
                .build();
        return companyRepository.save(company);
    }

    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(Role.builder().name(request.getRole()).build()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true) // Admin created users are pre-verified for now
                .company(company)
                .build();

        user.getRoles().add(role);
        return userRepository.save(user);
    }
}
