package com.grivetyglobals.invoiceiq.entity;

import com.grivetyglobals.invoiceiq.enums.DataScope;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "role_permission_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_group_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_group_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PermissionGroup permissionGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_scope", length = 30)
    private DataScope dataScope;
}
