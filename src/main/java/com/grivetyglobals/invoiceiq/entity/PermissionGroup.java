package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "permission_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PermissionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permission_group_id")
    private UUID id;

    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Organization organization;

    @OneToMany(mappedBy = "permissionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<PermissionGroupMapping> permissions = new java.util.HashSet<>();

    @OneToMany(mappedBy = "permissionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<RolePermissionGroup> rolePermissionGroups = new java.util.HashSet<>();
}
