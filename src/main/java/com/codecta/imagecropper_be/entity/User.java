package com.codecta.imagecropper_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Interni korisnik u bazi — mapiramo Google OIDC identitet na naš UUID.
 * Jedinstveno polje je providerUserId (Google "sub").
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_provider_user_id", columnNames = {"provider_user_id"}),
        @UniqueConstraint(name = "uq_users_email", columnNames = {"email"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // npr. "GOOGLE" (može ostati string da bude fleksibilno)
    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    // Google OIDC "sub" (stabilan, jedinstven ID per korisnik kod provajdera)
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
