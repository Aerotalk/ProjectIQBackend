package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.*;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.entity.RefreshToken;
import com.grivetyglobals.invoiceiq.entity.VerificationToken;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public void setupSuperAdmin(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Super admin email already exists");
        }

        Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_SUPER_ADMIN").build()));

        User superAdmin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true) // Auto-verified for bootstrap
                .organization(null) // Super Admins don't belong to a tenant
                .build();
        
        superAdmin.getRoles().add(superAdminRole);
        userRepository.save(superAdmin);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email address before logging in");
        }

        // Delete any existing refresh token for this user so they don't pile up
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush(); // Force Hibernate to delete BEFORE inserting the new one

        return createAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        String newJwtToken = jwtUtil.generateToken(refreshToken.getUser());
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

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete any old unused reset tokens
        verificationTokenRepository.deleteByUserAndTokenType(user, VerificationToken.TokenType.PASSWORD_RESET);

        VerificationToken resetToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 60 * 24)) // 24 hours
                .build();
        
        verificationTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
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

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        verificationTokenRepository.delete(resetToken);
    }

    private AuthResponse createAuthResponse(User user) {
        String jwtToken = jwtUtil.generateToken(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
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
