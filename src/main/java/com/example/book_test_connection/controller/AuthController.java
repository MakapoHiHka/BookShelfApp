package com.example.book_test_connection.controller;

import com.example.book_test_connection.dto.LoginRequest;
import com.example.book_test_connection.dto.RegisterRequest;
import com.example.book_test_connection.dto.JwtResponse;
import com.example.book_test_connection.service.AuthService;
import com.example.book_test_connection.service.UserService;
import com.example.book_test_connection.service.CaptchaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    //private final CaptchaService captchaService;

    public AuthController(UserService userService,
                          AuthService authService
                          ) { //CaptchaService captchaService
        this.userService = userService;
        this.authService = authService;
        //this.captchaService = captchaService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
//        captchaService.verify(request.getCaptchaResponse());
        userService.register(request);
        return ResponseEntity.ok("Check your email for confirmation link");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email confirmed!");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
