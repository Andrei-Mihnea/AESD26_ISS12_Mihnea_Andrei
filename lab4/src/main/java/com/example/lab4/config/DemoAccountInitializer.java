package com.example.lab4.config;

import com.example.lab4.entity.User;
import com.example.lab4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** One-time demo credentials stored in the database, never an in-memory authentication fallback. */
@Component
@Order(50)
@ConditionalOnProperty(name = "app.security.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class DemoAccountInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final String adminUsername, adminPassword, customerUsername, customerPassword, driverUsername, driverPassword;

    public DemoAccountInitializer(UserRepository users, PasswordEncoder passwords,
            @Value("${app.security.username}") String adminUsername,
            @Value("${app.security.password}") String adminPassword,
            @Value("${app.security.customer.username}") String customerUsername,
            @Value("${app.security.customer.password}") String customerPassword,
            @Value("${app.security.driver.username}") String driverUsername,
            @Value("${app.security.driver.password}") String driverPassword) {
        this.users = users;
        this.passwords = passwords;
        this.adminUsername = adminUsername; this.adminPassword = adminPassword;
        this.customerUsername = customerUsername; this.customerPassword = customerPassword;
        this.driverUsername = driverUsername; this.driverPassword = driverPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        provision(new User("Demo Administrator", "admin@example.com", "+40000000000"), adminUsername, adminPassword);
        provision(User.customer("Demo Customer", "demo@example.com", "+40123456789",
                "1 Example Street", "44.4320,26.1030"), customerUsername, customerPassword);
        provision(User.driver("Demo Driver", "driver@example.com", "+40987654321"), driverUsername, driverPassword);
    }

    private void provision(User candidate, String username, String rawPassword) {
        var existingLogin = users.findByUsername(username);
        if (existingLogin.isPresent()) {
            existingLogin.get().requireRole(candidate.getRole());
            return;
        }
        User user = users.findByEmailAndRole(candidate.getEmail(), candidate.getRole()).orElse(candidate);
        // Preserve configured passwords and explicitly disabled accounts on every restart.
        if (user.getUsername() != null) return;
        if (rawPassword.isBlank()) throw new IllegalArgumentException("Demo account password must not be blank");
        user.configureLogin(username, passwords.encode(rawPassword));
        users.save(user);
    }
}
