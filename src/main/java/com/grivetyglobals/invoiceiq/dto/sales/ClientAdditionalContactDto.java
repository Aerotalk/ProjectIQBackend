package com.grivetyglobals.invoiceiq.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAdditionalContactDto {
    private UUID id;
    private String name;
    private String designation;
    private String email;
    private String phone;
    private String role;
}
