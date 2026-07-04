package com.grivetyglobals.invoiceiq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

<<<<<<< Updated upstream
    @OneToOne
=======
    @OneToOne(fetch = FetchType.LAZY)
>>>>>>> Stashed changes
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;
}
