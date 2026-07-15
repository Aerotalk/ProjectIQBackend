package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeCreateRequest {

    private UUID organizationId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "First Name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private LocalDate joiningDate;

    private UUID departmentId;

    private UUID designationId;

    private UUID reportingManagerId;

    private UUID profilePicture;

    @NotBlank(message = "Employment Status is required")
    private String employmentStatus;
}
