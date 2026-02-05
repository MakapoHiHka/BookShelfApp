package com.example.book_test_connection.exceptions;

public class ShelfNotFoundException extends RuntimeException {
    public ShelfNotFoundException(String message) {
        super(message);
    }
}
