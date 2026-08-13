package com.grivetyglobals.invoiceiq.dto.hrms.performance;

import com.grivetyglobals.invoiceiq.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummaryDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String name;
    private String employeeCode;
    private DesignationDTO designation;
    private DepartmentDTO department;
    private UUID profilePicture;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DesignationDTO {
        private UUID id;
        private String designationName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentDTO {
        private UUID id;
        private String departmentName;
    }

    public static EmployeeSummaryDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }
        
        DesignationDTO designationDTO = null;
        if (employee.getDesignation() != null) {
            designationDTO = DesignationDTO.builder()
                    .id(employee.getDesignation().getId())
                    .designationName(employee.getDesignation().getDesignationName())
                    .build();
        }

        DepartmentDTO departmentDTO = null;
        if (employee.getDepartment() != null) {
            departmentDTO = DepartmentDTO.builder()
                    .id(employee.getDepartment().getId())
                    .departmentName(employee.getDepartment().getDepartmentName())
                    .build();
        }

        String name = "";
        if (employee.getFirstName() != null) {
            name = employee.getFirstName();
            if (employee.getLastName() != null) {
                name += " " + employee.getLastName();
            }
        }

        return EmployeeSummaryDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .name(name.trim())
                .employeeCode(employee.getEmployeeCode())
                .designation(designationDTO)
                .department(departmentDTO)
                .profilePicture(employee.getProfilePicture())
                .build();
    }
}
