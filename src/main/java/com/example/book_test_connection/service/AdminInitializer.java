package com.example.book_test_connection.service;

import com.example.book_test_connection.entity.User;
import com.example.book_test_connection.repository.UserRepository;
import com.example.book_test_connection.utils.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Пароль можно задать в application.properties: app.admin.password=admin123
    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createAdminUser() {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        }
        display(adminEmail, adminPassword);
    }

    private void display(String adminEmail, String adminPassword){
        System.out.println("========================================");
        System.out.println("ADMIN USER DATA");
        System.out.println("Email: " + adminEmail);
        System.out.println("Password: " + adminPassword);
        System.out.println("========================================");
    }
}