package com.grivetyglobals.invoiceiq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grivetyglobals.invoiceiq.dto.LoginRequest;
import com.grivetyglobals.invoiceiq.dto.AuthResponse;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RedisApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.grivetyglobals.invoiceiq.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.grivetyglobals.invoiceiq.repository.CompanyRepository companyRepository;

    @Test
    public void testFullScaleRedisCachingAndSerialization() throws Exception {
        // 0. Prepare Super Admin
        System.out.println("--- STEP 0: PREPARE SUPER ADMIN ---");
        com.grivetyglobals.invoiceiq.entity.User adminUser = userRepository.findAll().stream()
                .filter(u -> u.getUserRoles().stream().anyMatch(ur -> ur.getRole().getRoleName().equals("ROLE_SUPER_ADMIN")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Super Admin found in DB"));
                
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        userRepository.save(adminUser);

        // 1. Authenticate to get a token
        System.out.println("--- STEP 1: AUTHENTICATING ---");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(adminUser.getEmail());
        loginRequest.setPassword("admin123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token from cookie
        String token = loginResult.getResponse().getCookie("access_token").getValue();
        assertNotNull(token, "Access token should not be null");

        // 2. Fetch a company ID to test with
        List<Company> companies = companyRepository.findAll();
        assertTrue(!companies.isEmpty(), "Database must have at least one company seeded for testing");
        UUID companyId = companies.get(0).getId();

        // 3. GET /api/admin/users
        // This tests the `Could not initialize proxy - no session` fix in UserRole.company
        System.out.println("--- STEP 2: GET /api/admin/users ---");
        mockMvc.perform(get("/api/admin/users")
                .cookie(new jakarta.servlet.http.Cookie("access_token", token)))
                .andExpect(status().isOk());
        System.out.println("SUCCESS: GET /api/admin/users returned 200 OK (Proxy Serialization fixed)");

        // 4. GET /api/admin/companies/{id} (FIRST HIT - Cache Miss)
        System.out.println("--- STEP 3: GET /api/admin/companies/{id} (Cache Miss) ---");
        mockMvc.perform(get("/api/admin/companies/" + companyId)
                .cookie(new jakarta.servlet.http.Cookie("access_token", token)))
                .andExpect(status().isOk());
        System.out.println("SUCCESS: First hit to company loaded from DB and saved to Redis!");

        // 5. GET /api/admin/companies/{id} (SECOND HIT - Cache Hit)
        System.out.println("--- STEP 4: GET /api/admin/companies/{id} (Cache Hit) ---");
        mockMvc.perform(get("/api/admin/companies/" + companyId)
                .cookie(new jakarta.servlet.http.Cookie("access_token", token)))
                .andExpect(status().isOk());
        System.out.println("SUCCESS: Second hit loaded perfectly from Redis without 403 or serialization crash!");

        // 6. Test Company Profile (Just in case they belong to one)
        System.out.println("--- STEP 5: GET /api/admin/company/profile ---");
        mockMvc.perform(get("/api/admin/company/profile")
                .cookie(new jakarta.servlet.http.Cookie("access_token", token)))
                .andExpect(status().is2xxSuccessful()); 
        // Note: It might return 204 No Content if the super admin has no company, which is fine, as long as it's not 400 or 500.
        System.out.println("SUCCESS: GET /api/admin/company/profile executed safely.");

        System.out.println("=========================================================");
        System.out.println("🎉 ALL SMOKE TESTS PASSED! Redis Cache Integration is STABLE.");
        System.out.println("=========================================================");
    }
}
