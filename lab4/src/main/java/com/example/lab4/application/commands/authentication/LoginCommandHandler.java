package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.interfaces.AccessTokenIssuer;
import com.example.lab4.application.interfaces.CredentialsAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import com.example.lab4.application.dto.LoginRequest;
import com.example.lab4.application.dto.LoginResponse;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import com.example.lab4.domain.entities.User;
import com.example.lab4.domain.entities.RefreshToken;
import com.example.lab4.application.interfaces.RefreshTokenRepository;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class LoginCommandHandler implements CommandHandler<LoginCommand, LoginResponse> {
    private final CredentialsAuthenticator passwords;
    private final UserRepository users;
    private final AccessTokenIssuer tokens;
    private final long refreshTtlSeconds;
    private final RefreshTokenRepository refreshTokens;
    private final SecureRandom random = new SecureRandom();

    public LoginCommandHandler(CredentialsAuthenticator passwords, UserRepository users, AccessTokenIssuer tokens, @Value("${app.security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds, RefreshTokenRepository refreshTokens) {
        this.passwords = passwords;
        this.users = users;
        this.tokens = tokens;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.refreshTokens = refreshTokens;
    }

    @Override public Class<LoginCommand> messageType() { return LoginCommand.class; }

    @Transactional
    @Override
    public LoginResponse handle(LoginCommand command) {
        LoginRequest request = command.request();
        String username = passwords.authenticate(request.username(), request.password());
        var user = users.findByUsername(username).filter(account -> account.isEnabled())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        return issue(user, UUID.randomUUID(), Instant.now().plusSeconds(refreshTtlSeconds));
    }

    private LoginResponse issue(User user, UUID family, Instant refreshExpiresAt) {
        var access = tokens.issue(user);
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshToken(user, family, hash(refresh), hash(user.getPasswordHash()), refreshExpiresAt));
        return new LoginResponse(access.value(), "Bearer", access.expiresIn(), access.expiresAt(), user.getUsername(), user.getRole(), refresh, refreshExpiresAt);
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }
}
