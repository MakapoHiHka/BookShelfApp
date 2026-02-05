package com.example.book_test_connection.dto;

import jakarta.validation.constraints.NotBlank;


public class LoginRequest {
    @NotBlank private String email;
    @NotBlank private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}



