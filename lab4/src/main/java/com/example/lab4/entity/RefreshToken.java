package com.example.lab4.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Stores only hashes; raw refresh tokens are returned to the client once. */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false) private UUID familyId;
    @Column(nullable = false, unique = true, length = 64) private String tokenHash;
    @Column(nullable = false, length = 64) private String credentialFingerprint;
    @Column(nullable = false) private Instant expiresAt;
    private Instant usedAt;
    @Column(nullable = false) private boolean revoked;

    protected RefreshToken() {}
    public RefreshToken(User user, UUID familyId, String tokenHash, String credentialFingerprint, Instant expiresAt) {
        this.id = UUID.randomUUID(); this.user = user; this.familyId = familyId; this.tokenHash = tokenHash;
        this.credentialFingerprint = credentialFingerprint; this.expiresAt = expiresAt;
    }
    public User getUser() { return user; }
    public UUID getFamilyId() { return familyId; }
    public String getCredentialFingerprint() { return credentialFingerprint; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public boolean isRevoked() { return revoked; }
    public void consume(Instant now) { usedAt = now; }
}
