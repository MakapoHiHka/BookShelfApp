package com.example.book_test_connection.exceptions;

//у пользователя недостаточно прав
public class NotEnoughRightsException extends RuntimeException {
    public NotEnoughRightsException(String message) {
        super(message);
    }
}