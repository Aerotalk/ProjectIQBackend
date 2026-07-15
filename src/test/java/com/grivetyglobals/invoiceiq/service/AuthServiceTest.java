package com.grivetyglobals.invoiceiq.service;

import com.grivetyglobals.invoiceiq.dto.AuthResponse;
import com.grivetyglobals.invoiceiq.dto.LoginRequest;
import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.RefreshToken;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.repository.RefreshTokenRepository;
import com.grivetyglobals.invoiceiq.repository.RoleRepository;
import com.grivetyglobals.invoiceiq.repository.UserRepository;
import com.grivetyglobals.invoiceiq.repository.VerificationTokenRepository;
import com.grivetyglobals.invoiceiq.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encoded_password")
                .emailVerified(true)
                .userRoles(new java.util.HashSet<>())
                .build();
    }

    @Test
    void login_Success_ReturnsAuthResponse() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser)).thenReturn("mocked_jwt_token");
        when(permissionService.getEffectivePermissions(testUser)).thenReturn(Collections.emptySet());

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        assertNotNull(response.getRefreshToken());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenRepository).deleteByUser(testUser);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void login_EmailNotVerified_ThrowsException() {
        // Arrange
        testUser.setEmailVerified(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Please verify your email address before logging in", exception.getMessage());
        
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewToken() {
        // Arrange
        com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest request = new com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest();
        request.setRefreshToken("valid_refresh_token");

        RefreshToken tokenEntity = RefreshToken.builder()
                .token("valid_refresh_token")
                .user(testUser)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken())).thenReturn(Optional.of(tokenEntity));
        when(jwtUtil.generateToken(testUser)).thenReturn("new_jwt_token");

        // Act
        AuthResponse response = authService.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals("new_jwt_token", response.getToken());
        assertEquals("valid_refresh_token", response.getRefreshToken());
    }

    @Test
    void refreshToken_ExpiredToken_ThrowsException() {
        // Arrange
        com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest request = new com.grivetyglobals.invoiceiq.dto.RefreshTokenRequest();
        request.setRefreshToken("expired_refresh_token");

        RefreshToken tokenEntity = RefreshToken.builder()
                .token("expired_refresh_token")
                .user(testUser)
                .expiryDate(Instant.now().minusSeconds(3600)) // Expired
                .build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken())).thenReturn(Optional.of(tokenEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Refresh token expired", exception.getMessage());
        
        verify(refreshTokenRepository).delete(tokenEntity);
        verify(jwtUtil, never()).generateToken(any());
    }
}
