package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for saving an employee's emergency contact.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEmergencyContactRequest {

    private String name;
    private String relationship;
    private String phone;
    private String alternatePhone;
    private String email;
    private String address;
    private Boolean primaryContact;
}
