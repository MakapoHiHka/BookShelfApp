package com.example.book_test_connection.service;

import com.example.book_test_connection.dto.LoginRequest;
import com.example.book_test_connection.dto.JwtResponse;
import com.example.book_test_connection.entity.User;
import com.example.book_test_connection.exceptions.UserNotEnabledException;
import com.example.book_test_connection.repository.UserRepository;
import com.example.book_test_connection.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse login(LoginRequest request) {
        // Аутентифицируем пользователя (проверка email + пароля)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //Получаем email из аутентификации (гарантированно валидный)
        String email = authentication.getName();

        //Проверяем, подтверждён ли email
        boolean isEnabled = userRepository.findByEmail(email)
                .map(User::isEnabled)
                .orElse(false);

        if (!isEnabled) {
            throw new UserNotEnabledException("Please confirm your email first");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем JWT
        String jwt = jwtUtils.generateToken(request.getEmail());
        return new JwtResponse(jwt);
    }
}