package com.grivetyglobals.invoiceiq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {

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
}
