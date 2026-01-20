package com.example.book_test_connection.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";


    // Обязательный конструктор
    public JwtResponse(String token) {
        this.token = token;
    }

    // Геттеры
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
