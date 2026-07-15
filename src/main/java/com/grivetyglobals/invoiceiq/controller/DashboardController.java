package com.grivetyglobals.invoiceiq.controller;

import com.grivetyglobals.invoiceiq.dto.DashboardMetricsResponse;
import com.grivetyglobals.invoiceiq.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAuthority('dashboard.view')")
    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
}
