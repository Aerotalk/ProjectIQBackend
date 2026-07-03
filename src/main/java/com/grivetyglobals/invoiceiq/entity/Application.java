package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id")
    private UUID id;

    @Column(name = "application_code", length = 30, unique = true)
    private String applicationCode;

    @Column(name = "application_name", length = 100)
    private String applicationName;

    @Column(name = "application_route", length = 255)
    private String applicationRoute;

    @Column(name = "icon", length = 255)
    private String icon;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<Module> modules = new java.util.ArrayList<>();
}
