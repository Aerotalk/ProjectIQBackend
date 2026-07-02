package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.AuthResponse;
import com.grivetyglobals.invoiceiq.dto.LoginRequest;
import com.grivetyglobals.invoiceiq.dto.RegisterRequest;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (companyRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Company company = Company.builder()
                .name(request.getName())
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .country(request.getCountry())
                .state(request.getState())
                .district(request.getDistrict())
                .pincode(request.getPincode())
                .gst(request.getGst())
                .address(request.getAddress())
                .companyPhoneNo(request.getCompanyPhoneNo())
                .build();

        companyRepository.save(company);
        
        String jwtToken = jwtUtil.generateToken(company);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtUtil.generateToken(company);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
