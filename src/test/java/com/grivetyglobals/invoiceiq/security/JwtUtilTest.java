package com.grivetyglobals.invoiceiq.security;

import com.grivetyglobals.invoiceiq.entity.Organization;
import com.grivetyglobals.invoiceiq.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Use a key >= 256 bits for HS256 algorithm
    private final String SECRET = "a-very-long-secret-key-that-is-at-least-256-bits-long-for-testing";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Manually inject the @Value field using reflection
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        // Arrange
        User user = User.builder().email("testuser@example.com").userRoles(new java.util.HashSet<>()).build();

        // Act
        String token = jwtUtil.generateToken(user);

        // Assert
        assertNotNull(token);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser@example.com", username);
    }

    @Test
    void testValidateToken_Success() {
        // Arrange
        User user = User.builder().email("validuser@example.com").userRoles(new java.util.HashSet<>()).build();

        // Act
        String token = jwtUtil.generateToken(user);
        Boolean isValid = jwtUtil.validateToken(token, user);

        // Assert
        assertTrue(isValid, "Token should be valid for the correct user");
    }

    @Test
    void testValidateToken_Failure_WrongUser() {
        // Arrange
        User user1 = User.builder().email("user1@example.com").userRoles(new java.util.HashSet<>()).build();
        User user2 = User.builder().email("user2@example.com").userRoles(new java.util.HashSet<>()).build();

        // Act
        String token = jwtUtil.generateToken(user1);
        Boolean isValid = jwtUtil.validateToken(token, user2);

        // Assert
        assertFalse(isValid, "Token should be invalid for a different user");
    }

    @Test
    void testRolesAndOrganizationIncludedInClaims() {
        // Arrange
        Organization org = new Organization();
        org.setId(UUID.randomUUID());

        com.grivetyglobals.invoiceiq.entity.Role role = new com.grivetyglobals.invoiceiq.entity.Role();
        role.setRoleName("ROLE_ADMIN");
        
        com.grivetyglobals.invoiceiq.entity.UserRole userRole = new com.grivetyglobals.invoiceiq.entity.UserRole();
        userRole.setRole(role);

        User user = User.builder()
                .email("admin@example.com")
                .userRoles(new java.util.HashSet<>(List.of(userRole)))
                .organization(org)
                .build();

        // Act
        String token = jwtUtil.generateToken(user);
        
        // Assert
        assertNotNull(token);
        
        List<?> roles = jwtUtil.extractClaim(token, claims -> claims.get("roles", List.class));
        String orgId = jwtUtil.extractClaim(token, claims -> claims.get("organizationId", String.class));
        
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertEquals(org.getId().toString(), orgId);
    }
}
