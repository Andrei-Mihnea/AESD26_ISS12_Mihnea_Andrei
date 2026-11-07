package com.example.lab4.service;

import com.example.lab4.config.JwtProperties;
import com.example.lab4.dto.LoginRequest;
import com.example.lab4.dto.LoginResponse;
import com.example.lab4.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import com.example.lab4.entity.User;
import com.example.lab4.entity.RefreshToken;
import com.example.lab4.repository.RefreshTokenRepository;
import com.example.lab4.exception.InvalidRefreshTokenException;

@Service
public class LoginService {
    private final DaoAuthenticationProvider passwords;
    private final UserRepository users;
    private final JwtEncoder tokens;
    private final JwtProperties settings;
    private final RefreshTokenRepository refreshTokens;
    private final SecureRandom random = new SecureRandom();

    public LoginService(DaoAuthenticationProvider passwords, UserRepository users, JwtEncoder tokens, JwtProperties settings, RefreshTokenRepository refreshTokens) {
        this.passwords = passwords; this.users = users; this.tokens = tokens; this.settings = settings; this.refreshTokens = refreshTokens;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        var authenticated = passwords.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                request.username(), request.password()));
        var user = users.findByUsername(authenticated.getName()).filter(account -> account.isEnabled())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        return issue(user, UUID.randomUUID(), Instant.now().plusSeconds(settings.refreshTtlSeconds()));
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public LoginResponse refresh(String rawToken) {
        // Serialize refresh, replay handling and logout for this user, including different family rows.
        Long userId = refreshTokens.findUserIdByHash(hash(rawToken)).orElseThrow(InvalidRefreshTokenException::new);
        users.findForUpdate(userId).orElseThrow(InvalidRefreshTokenException::new);
        RefreshToken previous = refreshTokens.findForUpdate(hash(rawToken)).orElseThrow(InvalidRefreshTokenException::new);
        User user = previous.getUser();
        if (previous.isRevoked() || previous.getUsedAt() != null || !previous.getExpiresAt().isAfter(Instant.now())
                || !user.isEnabled() || !previous.getCredentialFingerprint().equals(hash(user.getPasswordHash()))) {
            refreshTokens.revokeFamily(previous.getFamilyId());
            throw new InvalidRefreshTokenException();
        }
        previous.consume(Instant.now());
        return issue(user, previous.getFamilyId(), previous.getExpiresAt());
    }

    @Transactional
    public void logout(String rawToken) {
        var userId = refreshTokens.findUserIdByHash(hash(rawToken));
        if (userId.isEmpty()) return;
        users.findForUpdate(userId.get());
        refreshTokens.findForUpdate(hash(rawToken)).ifPresent(token -> refreshTokens.revokeFamily(token.getFamilyId()));
    }

    private LoginResponse issue(User user, UUID family, Instant refreshExpiresAt) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(settings.ttlSeconds());
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(settings.issuer()).subject(user.getUsername())
                .audience(List.of(settings.audience())).issuedAt(issuedAt).notBefore(issuedAt).expiresAt(expiresAt)
                .id(UUID.randomUUID().toString()).claim("uid", user.getId())
                .claim("roles", List.of(user.getRole().name())).claim("token_use", "access").build();
        String token = tokens.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims)).getTokenValue();
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshToken(user, family, hash(refresh), hash(user.getPasswordHash()), refreshExpiresAt));
        return new LoginResponse(token, "Bearer", settings.ttlSeconds(), expiresAt, user.getUsername(), user.getRole(), refresh, refreshExpiresAt);
    }
    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }
}

