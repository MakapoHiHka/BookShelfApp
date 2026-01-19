package com.example.book_test_connection.controller;

import com.example.book_test_connection.dto.BookCreateRequest;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books → получить все книги
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    // GET /api/books/1 → получить книгу по ID
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    // POST /api/books → создать новую книгу
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookCreateRequest request, UriComponentsBuilder uriBuilder) {
        Book savedBook = bookService.createBook(request);
        var location = uriBuilder.path("/api/books/{id}").buildAndExpand(savedBook.getId()).toUri();
        return ResponseEntity.created(location).body(savedBook);
    }

    // PUT /api/books/1 → полностью обновить книгу
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @Valid @RequestBody BookCreateRequest request) {
        return bookService.updateBook(id, request);
    }

    // DELETE /api/books/1 → удалить книгу
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}