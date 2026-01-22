package com.example.book_test_connection.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class BookCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Author is required")
    private String author;

    private String description;

    private MultipartFile file;
    // геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}