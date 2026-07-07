package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permission_id")
    private UUID id;

    @Column(name = "permission_key", length = 255, nullable = false, unique = true)
    private String permissionKey;

    @Column(name = "permission_name", length = 255, nullable = false)
    private String permissionName;

    @Column(length = 100, nullable = false)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;
}
