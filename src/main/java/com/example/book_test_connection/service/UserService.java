package com.example.book_test_connection.service;


import com.example.book_test_connection.dto.RegisterRequest;
import com.example.book_test_connection.entity.ConfirmationToken;
import com.example.book_test_connection.entity.User;
import com.example.book_test_connection.exceptions.EmailAlreadyExistsException;
import com.example.book_test_connection.exceptions.UsernameAlreadyExistsException;
import com.example.book_test_connection.repository.ConfirmationTokenRepository;
import com.example.book_test_connection.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ConfirmationTokenRepository tokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        ConfirmationToken token = new ConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(savedUser);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));

        tokenRepository.save(token);

        emailService.sendConfirmationEmail(user.getEmail(), token.getToken());
    }

    public void confirmEmail(String token) {
        Optional<ConfirmationToken> confirmationTokenOpt = tokenRepository.findByToken(token);
        if (confirmationTokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid confirmation token");
        }

        ConfirmationToken confirmationToken = confirmationTokenOpt.get();
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Confirmation token has expired");
        }

        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}