package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.User;
import java.util.List;
import java.util.Optional;
import com.example.lab4.domain.enums.Role;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface UserRepository {
    Optional<User> findById(Long id);
    List<User> findAll();
    <S extends User> S save(S entity);
    <S extends User> List<S> saveAll(Iterable<S> entities);
    void delete(User entity);
    void deleteById(Long id);
    void flush();
    long count();
    Optional<User> findForUpdate(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmailAndRole(String email, Role role);
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndRole(Long id, Role role);
    List<User> findAllByRole(Role role);
    Optional<User> findByEmailAndRole(String email, Role role);
    <S extends User> S saveAndFlush(S entity);
}
