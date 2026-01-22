package com.example.book_test_connection.controller;

import com.example.book_test_connection.dto.BookCreateRequest;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.exceptions.UploadErrorException;
import com.example.book_test_connection.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    //создать книгу с файлом
    @PostMapping("/upload")
    public ResponseEntity<Book> uploadBookWithFile(
            @RequestParam("name") String name,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            UriComponentsBuilder uriBuilder
    ) {
        if (file.isEmpty()) {
            throw new UploadErrorException("File cannot be empty");
        }

        BookCreateRequest request = new BookCreateRequest();
        request.setName(name);
        request.setAuthor(author);
        request.setDescription(description);

        Book savedBook = bookService.createBookWithFile(request, file);
        var location = uriBuilder.path("/api/books/{id}").buildAndExpand(savedBook.getId()).toUri();
        return ResponseEntity.created(location).body(savedBook);
    }

    //прикрепить файл к уже созданной книге
    @PostMapping("/{id}/upload-file")
    public ResponseEntity<Book> uploadFileToExistingBook(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Book updatedBook = bookService.attachFileToBook(id, file);
        return ResponseEntity.ok(updatedBook);
    }
}