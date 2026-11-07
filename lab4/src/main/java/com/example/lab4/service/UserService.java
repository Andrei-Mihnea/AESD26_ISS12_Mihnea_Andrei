package com.example.lab4.service;

import com.example.lab4.entity.Role;
import com.example.lab4.entity.User;
import com.example.lab4.repository.UserRepository;
import com.example.lab4.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository users;

    private final PasswordEncoder passwords;

    public UserService(UserRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public List<User> findAll() { return users.findAll(); }
    public List<User> findByRole(Role role) { return users.findAllByRole(role); }
    public Optional<User> findById(Long id) { return users.findById(id); }

    @Transactional
    public void configureLogin(Long id, String username, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("Password is required");
        User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.configureLogin(username, passwords.encode(rawPassword));
    }

    @Transactional
    public void disableLogin(Long id) {
        users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)).disableLogin();
    }

    @Transactional
    public User createAdministrator(String name, String email, String phone) {
        return users.save(new User(name, email, phone));
    }
}
