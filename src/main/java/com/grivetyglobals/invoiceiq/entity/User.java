package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@org.hibernate.annotations.BatchSize(size = 20)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(length = 100, nullable = false)
    private String username;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String mobile;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "email_verified", columnDefinition = "boolean default false", nullable = false)
    private boolean emailVerified = false;

    @Builder.Default
    @Column(name = "mobile_verified", columnDefinition = "boolean default false", nullable = false)
    private boolean mobileVerified = false;

    @Builder.Default
    @Column(name = "mfa_enabled", columnDefinition = "boolean default false", nullable = false)
    private boolean mfaEnabled = false;

    @Builder.Default
    @Column(name = "account_locked", columnDefinition = "boolean default false", nullable = false)
    private boolean accountLocked = false;

    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;

    @Builder.Default
    @Column(name = "failed_login_attempts", columnDefinition = "integer default 0", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(length = 20)
    private String status;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserApplication> userApplications = new HashSet<>();

    @Transient
    @Builder.Default
    private Set<String> effectivePermissions = new HashSet<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName()))
                .collect(Collectors.toSet());
                
        if (effectivePermissions != null) {
            for (String perm : effectivePermissions) {
                authorities.add(new SimpleGrantedAuthority(perm));
            }
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
