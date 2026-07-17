package com.grivetyglobals.invoiceiq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grivetyglobals.invoiceiq.dto.RoleRequest;
import com.grivetyglobals.invoiceiq.entity.Role;
import com.grivetyglobals.invoiceiq.service.RoleService;
import com.grivetyglobals.invoiceiq.service.PermissionService;
import com.grivetyglobals.invoiceiq.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {RoleController.class, PermissionController.class}, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for basic controller mapping testing
class RolePermissionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private com.grivetyglobals.invoiceiq.security.JwtFilter jwtFilter;

    @MockitoBean
    private com.grivetyglobals.invoiceiq.security.UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void createRole_AcceptsFrontendData() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("TEST_ROLE");
        request.setDescription("A test role from frontend");

        Role mockRole = new Role();
        mockRole.setId(UUID.randomUUID());
        mockRole.setRoleName("TEST_ROLE");
        mockRole.setDescription("A test role from frontend");

        when(roleService.createRole(any(RoleRequest.class))).thenReturn(mockRole);

        mockMvc.perform(post("/api/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("TEST_ROLE"))
                .andExpect(jsonPath("$.description").value("A test role from frontend"));
    }

    @Test
    void getPermissionMatrix_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/permissions/matrix")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateRolePermissions_AcceptsFrontendData() throws Exception {
        UUID roleId = UUID.randomUUID();
        Set<UUID> permissionIds = new HashSet<>();
        permissionIds.add(UUID.randomUUID());
        permissionIds.add(UUID.randomUUID());

        Role mockRole = new Role();
        mockRole.setId(roleId);
        mockRole.setRoleName("UPDATED_ROLE");

        when(permissionService.updateRolePermissions(eq(roleId), any(Set.class))).thenReturn(mockRole);

        mockMvc.perform(put("/api/admin/permissions/roles/{roleId}", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()));
    }
}
