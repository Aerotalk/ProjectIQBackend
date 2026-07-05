package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsResponse {
    
    private long totalCompanies;
    private long totalEmployees;
    private long totalDepartments;
    private long totalRoles;
    
    // MOCKED DATA
    private long totalApplications;
    private List<Object> recentActivityLogs;
    
    private String systemNotice;
}
