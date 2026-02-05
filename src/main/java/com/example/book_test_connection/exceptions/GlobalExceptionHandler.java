package com.example.book_test_connection.exceptions;

import com.example.book_test_connection.service.HtmlConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //если пароль или почта неправильная
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
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

    //книга не найдена
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }

    @ExceptionHandler(UploadErrorException.class)
    public ResponseEntity<String> handleUploadErrorException(UploadErrorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }

    @ExceptionHandler(NotEnoughRightsException.class)
    public ResponseEntity<String> handleNotEnoughRights(NotEnoughRightsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnableFormatException.class)
    public ResponseEntity<String> handleUnableFormatException(UnableFormatException ex) {
        log.warn("Не найден конвертер: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }

    @ExceptionHandler(CaptchaException.class)
    public ResponseEntity<String> handleCaptchaException(CaptchaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }

    @ExceptionHandler(ShelfNotFoundException.class)
    public ResponseEntity<String> handleShelfNotFoundException(ShelfNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) //FORBIDDEN
                .body(ex.getMessage());
    }
}