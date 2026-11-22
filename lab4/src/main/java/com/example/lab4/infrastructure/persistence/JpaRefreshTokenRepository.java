package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.RefreshToken;
import com.example.lab4.application.interfaces.RefreshTokenRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, UUID>, RefreshTokenRepository {
    @Query("select token.user.id from RefreshToken token where token.tokenHash = :hash")
    Optional<Long> findUserIdByHash(@Param("hash") String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token where token.tokenHash = :hash")
    Optional<RefreshToken> findForUpdate(@Param("hash") String hash);

    @Modifying(flushAutomatically = true)
    @Query("update RefreshToken token set token.revoked = true where token.familyId = :family")
    int revokeFamily(@Param("family") UUID family);
}
