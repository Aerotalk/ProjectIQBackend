package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "permission_group_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionGroupMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mapping_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_group_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PermissionGroup permissionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}
