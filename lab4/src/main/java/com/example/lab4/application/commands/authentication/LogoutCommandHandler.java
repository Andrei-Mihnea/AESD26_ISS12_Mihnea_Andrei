package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import com.example.lab4.application.interfaces.RefreshTokenRepository;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class LogoutCommandHandler implements CommandHandler<LogoutCommand, Void> {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;

    public LogoutCommandHandler(UserRepository users, RefreshTokenRepository refreshTokens) {
        this.users = users;
        this.refreshTokens = refreshTokens;
    }

    @Override public Class<LogoutCommand> messageType() { return LogoutCommand.class; }

    @Transactional
    @Override
    public Void handle(LogoutCommand command) {
        String rawToken = command.rawToken();
        var userId = refreshTokens.findUserIdByHash(hash(rawToken));
        if (userId.isEmpty()) return null;
        users.findForUpdate(userId.get());
        refreshTokens.findForUpdate(hash(rawToken)).ifPresent(token -> refreshTokens.revokeFamily(token.getFamilyId()));

        return null;
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }
}
