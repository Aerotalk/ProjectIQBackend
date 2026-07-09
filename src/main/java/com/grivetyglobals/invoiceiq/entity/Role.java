package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Company company;

    @Column(name = "role_name", length = 100, nullable = false)
    private String roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "system_role")
    @Builder.Default
    private Boolean systemRole = false;

    @Column(length = 20)
    private String status;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<RolePermission> rolePermissions = new java.util.HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<RolePermissionGroup> rolePermissionGroups = new java.util.HashSet<>();
}
