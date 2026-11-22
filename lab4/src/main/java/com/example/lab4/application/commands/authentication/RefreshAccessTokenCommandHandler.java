package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.interfaces.AccessTokenIssuer;
import com.example.lab4.application.dto.LoginResponse;
import com.example.lab4.application.interfaces.UserRepository;
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
import com.example.lab4.application.exceptions.InvalidRefreshTokenException;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class RefreshAccessTokenCommandHandler implements CommandHandler<RefreshAccessTokenCommand, LoginResponse> {
    private final UserRepository users;
    private final AccessTokenIssuer tokens;
    private final RefreshTokenRepository refreshTokens;
    private final SecureRandom random = new SecureRandom();

    public RefreshAccessTokenCommandHandler(UserRepository users, AccessTokenIssuer tokens, RefreshTokenRepository refreshTokens) {
        this.users = users;
        this.tokens = tokens;
        this.refreshTokens = refreshTokens;
    }

    @Override public Class<RefreshAccessTokenCommand> messageType() { return RefreshAccessTokenCommand.class; }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    @Override
    public LoginResponse handle(RefreshAccessTokenCommand command) {
        String rawToken = command.rawToken();

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
