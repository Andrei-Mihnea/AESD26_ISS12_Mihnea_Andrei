package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.RefreshToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface RefreshTokenRepository {
    Optional<RefreshToken> findById(UUID id);
    List<RefreshToken> findAll();
    <S extends RefreshToken> S save(S entity);
    <S extends RefreshToken> List<S> saveAll(Iterable<S> entities);
    void delete(RefreshToken entity);
    void deleteById(UUID id);
    void flush();
    long count();
    Optional<Long> findUserIdByHash(String hash);
    Optional<RefreshToken> findForUpdate(String hash);
    int revokeFamily(UUID family);
}
