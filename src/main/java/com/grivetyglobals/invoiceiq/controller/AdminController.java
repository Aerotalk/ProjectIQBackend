package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.CompanyCreateRequest;
import com.grivetyglobals.invoiceiq.dto.UserCreateRequest;
import com.grivetyglobals.invoiceiq.entity.Company;
import com.grivetyglobals.invoiceiq.entity.User;
import com.grivetyglobals.invoiceiq.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyCreateRequest request) {
        return ResponseEntity.ok(adminService.createCompany(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }
}
