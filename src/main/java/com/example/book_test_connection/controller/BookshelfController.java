package com.example.book_test_connection.controller;

import com.example.book_test_connection.dto.BookshelfDto;
import com.example.book_test_connection.dto.CreateBookshelfRequest;
import com.example.book_test_connection.entity.Bookshelf;
import com.example.book_test_connection.service.BookshelfService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookshelves")
public class BookshelfController {

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }
    @GetMapping
    public List<BookshelfDto> getUserBookshelves() {
        Long currentUserId = getCurrentUserId();
        List<Bookshelf> shelves = bookshelfService.findByUserId(currentUserId);
        return shelves.stream().map(BookshelfDto::new).toList();
    }

    // Создать новую полку
    @PostMapping
    public ResponseEntity<BookshelfDto> createBookshelf(@Valid @RequestBody CreateBookshelfRequest request) {
        Long currentUserId = getCurrentUserId();
        Bookshelf bookshelf = bookshelfService.createBookshelf(request.getName(), currentUserId);
        return ResponseEntity.ok(new BookshelfDto(bookshelf)); // ← обёрнут в DTO
    }



    // Добавить книгу в полку
    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<Void> addBookToShelf(
            @PathVariable Long shelfId,
            @PathVariable Long bookId
    ) {
        Long currentUserId = getCurrentUserId();
        bookshelfService.addBookToShelf(shelfId, bookId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // Удалить книгу из полки
    @DeleteMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromShelf(
            @PathVariable Long shelfId,
            @PathVariable Long bookId
    ) {
        Long currentUserId = getCurrentUserId();
        bookshelfService.removeBookFromShelf(shelfId, bookId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // Удалить всю полку
    @DeleteMapping("/{shelfId}")
    public ResponseEntity<Void> deleteBookshelf(@PathVariable Long shelfId) {
        Long currentUserId = getCurrentUserId();
        bookshelfService.deleteBookshelf(shelfId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // Вспомогательный метод: получает ID текущего пользователя
    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return bookshelfService.getUserIdByEmail(email);
    }
}
