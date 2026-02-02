package com.example.book_test_connection.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBookshelfRequest {

    @NotBlank(message = "Name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}