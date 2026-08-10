package com.grivetyglobals.invoiceiq.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for EmployeeUpdateRequest.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeUpdateRequest {

    private UUID companyId;

    @NotBlank(message = "First Name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    @JsonAlias({"dateOfJoining", "joiningDate"})
    private LocalDate joiningDate;

    private UUID departmentId;

    private UUID designationId;

    private UUID reportingManagerId;

    @JsonAlias({"profilePhoto", "profilePicture"})
    private UUID profilePicture;

    private String maritalStatus;
    private String bloodGroup;
    private String nationality;
    private String employmentType;
    private String location;
    private String grade;
    private UUID hrManagerId;
    private String weeklyOff;
    private String fatherName;
    private Integer noticePeriodDays;
    private String alternatePhone;
    private String workEmail;
    private String phone;
}

