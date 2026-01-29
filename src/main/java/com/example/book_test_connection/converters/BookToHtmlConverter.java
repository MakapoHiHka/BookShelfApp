package com.example.book_test_connection.converters;

import com.example.book_test_connection.dto.ConvertationResult;

import java.nio.file.Path;

public interface BookToHtmlConverter {
    boolean supports(String extension);
    ConvertationResult convertToHtml(Path sourcePath, Long bookId) throws Exception;
}