package com.grivetyglobals.invoiceiq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCreateRequest {
    private String companyName;
    private String country;
    private String state;
    private String district;
    private String pincode;
    private String gst;
    private String address;
    private String companyPhoneNo;
}
