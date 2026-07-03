package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.*;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.RefreshToken;
import com.grivetyglobals.invoiceiq.entity.VerificationToken;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import com.grivetyglobals.invoiceiq.repository.RefreshTokenRepository;
import com.grivetyglobals.invoiceiq.repository.VerificationTokenRepository;
import com.grivetyglobals.invoiceiq.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
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
        
        // Generate Email Verification Token
        VerificationToken verificationToken = VerificationToken.builder()
                .company(company)
                .token(UUID.randomUUID().toString())
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 60 * 24)) // 24 hours
                .build();
        verificationTokenRepository.save(verificationToken);

        // Send Email
        emailService.sendVerificationEmail(company.getEmail(), verificationToken.getToken());
        
        return createAuthResponse(company);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!company.isEmailVerified()) {
            throw new RuntimeException("Please verify your email address before logging in");
        }

        // Delete any existing refresh token for this user so they don't pile up
        refreshTokenRepository.deleteByCompany(company);
        refreshTokenRepository.flush(); // Force Hibernate to delete BEFORE inserting the new one

        return createAuthResponse(company);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        String newJwtToken = jwtUtil.generateToken(refreshToken.getCompany());
        return AuthResponse.builder()
                .token(newJwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getTokenType() != VerificationToken.TokenType.EMAIL_VERIFICATION) {
            throw new RuntimeException("Invalid token type");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Verification token expired");
        }

        Company company = verificationToken.getCompany();
        company.setEmailVerified(true);
        companyRepository.save(company);

        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete any old unused reset tokens
        verificationTokenRepository.deleteByCompanyAndTokenType(company, VerificationToken.TokenType.PASSWORD_RESET);

        VerificationToken resetToken = VerificationToken.builder()
                .company(company)
                .token(UUID.randomUUID().toString())
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 60 * 24)) // 24 hours
                .build();
        
        verificationTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(company.getEmail(), resetToken.getToken());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        VerificationToken resetToken = verificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.getTokenType() != VerificationToken.TokenType.PASSWORD_RESET) {
            throw new RuntimeException("Invalid token type");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token expired");
        }

        Company company = resetToken.getCompany();
        company.setPassword(passwordEncoder.encode(request.getNewPassword()));
        companyRepository.save(company);

        verificationTokenRepository.delete(resetToken);
    }

    private AuthResponse createAuthResponse(Company company) {
        String jwtToken = jwtUtil.generateToken(company);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .company(company)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7)) // 7 days
                .build();
                
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
