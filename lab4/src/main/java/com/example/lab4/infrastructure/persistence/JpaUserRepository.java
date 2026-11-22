package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select user from User user where user.id = :id")
    Optional<User> findForUpdate(@Param("id") Long id);

    boolean existsByUsername(String username);
    boolean existsByEmailAndRole(String email, Role role);
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndRole(Long id, Role role);
    List<User> findAllByRole(Role role);
    Optional<User> findByEmailAndRole(String email, Role role);
}
