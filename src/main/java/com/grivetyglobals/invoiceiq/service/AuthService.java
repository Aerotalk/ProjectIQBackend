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

/**
 * Service class for handling authentication, authorization, and user account management.
 * Manages login, tokens, password resets, and profile retrieval.
 */
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
    private final PermissionService permissionService;

    /**
     * Bootstraps the initial super admin user in the system.
     * 
     * @param request the registration details
     * @throws RuntimeException if a super admin already exists
     */
    @Transactional
    public void setupSuperAdmin(RegisterRequest request) {
        if (userRepository.existsByUserRoles_Role_RoleName("ROLE_SUPER_ADMIN")) {
            throw new RuntimeException("A Super admin already exists in the system");
        }

        Role superAdminRole = roleRepository.findByRoleName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_SUPER_ADMIN").systemRole(true).build()));

        User superAdmin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(true) // Auto-verified for bootstrap
                .organization(null) // Super Admins don't belong to a tenant
                .build();
        
        com.grivetyglobals.invoiceiq.entity.UserRole userRole = com.grivetyglobals.invoiceiq.entity.UserRole.builder()
                .user(superAdmin)
                .role(superAdminRole)
                .build();
        superAdmin.getUserRoles().add(userRole);
        userRepository.save(superAdmin);
    }

    /**
     * Authenticates a user and generates JWT and refresh tokens.
     * 
     * @param request the login credentials
     * @return an AuthResponse containing tokens and user context
     */
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

    /**
     * Refreshes an expired JWT using a valid refresh token.
     * 
     * @param request the refresh token payload
     * @return a new AuthResponse with fresh tokens
     */
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

    /**
     * Logs out a user by invalidating their refresh token.
     * 
     * @param request the logout payload containing the refresh token
     */
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Verifies a user's email address using a verification token.
     * 
     * @param token the email verification token
     */
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

    /**
     * Initiates the password reset process by generating a token and sending an email.
     * 
     * @param request the forgot password request containing the user's email
     */
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

    /**
     * Resets a user's password using a valid password reset token.
     * 
     * @param request the reset password request payload
     */
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

        java.util.List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .collect(java.util.stream.Collectors.toList());

        java.util.Set<String> effectivePermissions = permissionService.getEffectivePermissions(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getActualUsername())
                .roles(roles)
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getOrganizationName() : null)
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .effectivePermissions(effectivePermissions)
                .profilePhotoId(user.getProfilePhotoId())
                .companyLogoId(user.getCompany() != null ? user.getCompany().getLogoFileId() : null)
                .primaryColor(user.getCompany() != null ? user.getCompany().getPrimaryColor() : null)
                .secondaryColor(user.getCompany() != null ? user.getCompany().getSecondaryColor() : null)
                .build();
    }

    /**
     * Retrieves the profile and context of the currently authenticated user.
     * 
     * @return a MeResponse containing user details and permissions
     */
    @Transactional(readOnly = true)
    public MeResponse getMe() {
        User user = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser();
        
        // Fetch full user entity to avoid LazyInitializationException
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        java.util.List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(java.util.stream.Collectors.toList());

        java.util.Set<String> effectivePermissions = permissionService.getEffectivePermissions(user);

        return MeResponse.builder()
                .id(user.getId())
                .username(user.getActualUsername())
                .email(user.getEmail())
                .roles(roles)
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getOrganizationName() : null)
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getCompanyName() : null)
                .effectivePermissions(effectivePermissions)
                .profilePhotoId(user.getProfilePhotoId())
                .companyLogoId(user.getCompany() != null ? user.getCompany().getLogoFileId() : null)
                .primaryColor(user.getCompany() != null ? user.getCompany().getPrimaryColor() : null)
                .secondaryColor(user.getCompany() != null ? user.getCompany().getSecondaryColor() : null)
                .build();
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * 
     * @param request the profile update payload
     * @return an updated MeResponse
     */
    @Transactional
    public MeResponse updateProfile(com.grivetyglobals.invoiceiq.dto.UpdateProfileRequest request) {
        User user = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser();
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        // Allow setting to null (removing photo) or updating to a new file ID
        user.setProfilePhotoId(request.getProfilePhotoId());

        userRepository.save(user);
        return getMe();
    }

    /**
     * Retrieves a list of companies the currently authenticated user has access to.
     * 
     * @return a list of maps containing company IDs and names
     */
    @Transactional(readOnly = true)
    public java.util.List<java.util.Map<String, Object>> getMyCompanies() {
        User user = com.grivetyglobals.invoiceiq.security.SecurityUtils.getCurrentUser();
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Collect distinct companies from the user's role assignments
        java.util.Map<java.util.UUID, com.grivetyglobals.invoiceiq.entity.Company> companyMap = new java.util.LinkedHashMap<>();
        for (com.grivetyglobals.invoiceiq.entity.UserRole ur : user.getUserRoles()) {
            if (ur.getCompany() != null) {
                companyMap.putIfAbsent(ur.getCompany().getId(), ur.getCompany());
            }
        }

        // If the user has a direct company association (e.g. auto-created company admin), include it too
        if (user.getCompany() != null) {
            companyMap.putIfAbsent(user.getCompany().getId(), user.getCompany());
        }

        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (com.grivetyglobals.invoiceiq.entity.Company c : companyMap.values()) {
            java.util.Map<String, Object> entry = new java.util.LinkedHashMap<>();
            entry.put("id", c.getId());
            entry.put("companyName", c.getCompanyName());
            result.add(entry);
        }
        return result;
    }
}
