package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    private String country;
    private String state;
    private String district;
    private String pincode;
    private String gst;
    private String address;
    private String companyPhoneNo;
}
