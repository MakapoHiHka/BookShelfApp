package com.example.book_test_connection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Пользователь не найден (можно использовать вместо BadCredentialsException)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    // Email уже занят
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    // Ошибки подтверждения email (токен недействителен/просрочен)
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<String> handleTokenError(TokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    // Имя пользовтеля уже используется
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    // Попытка входа без подтверждения email
    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<String> handleUserNotEnabled(UserNotEnabledException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }
}