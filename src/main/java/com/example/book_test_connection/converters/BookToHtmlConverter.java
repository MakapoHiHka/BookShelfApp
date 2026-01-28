package com.example.book_test_connection.converters;

import java.nio.file.Path;

public interface BookToHtmlConverter {
    boolean supports(String extension);
    String convertToHtml(Path sourcePath) throws Exception;
}