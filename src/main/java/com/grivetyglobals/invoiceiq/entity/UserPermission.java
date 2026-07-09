package com.grivetyglobals.invoiceiq.entity;

import com.grivetyglobals.invoiceiq.enums.DataScope;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_permission_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(name = "is_granted", nullable = false)
    private boolean isGranted; // True for grant override, False for revoke override

    @Enumerated(EnumType.STRING)
    @Column(name = "data_scope", length = 30)
    private DataScope dataScope;
}
