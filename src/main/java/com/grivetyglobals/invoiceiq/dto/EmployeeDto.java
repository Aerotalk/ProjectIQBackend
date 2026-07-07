package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private UUID id;
    private UUID organizationId;
    private UUID userId;
    private String userEmail;
    private String employeeCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    
    private UUID departmentId;
    private String departmentName;
    
    private UUID designationId;
    private String designationName;
    
    private UUID reportingManagerId;
    private String reportingManagerName;
    
    private UUID profilePicture;
    private String employmentStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
