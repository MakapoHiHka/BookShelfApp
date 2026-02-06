package com.example.book_test_connection.controller;

import com.example.book_test_connection.dto.BookCreateRequest;
import com.example.book_test_connection.dto.BookProgressUpdateRequest;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.entity.BookProgress;
import com.example.book_test_connection.exceptions.NotEnoughRightsException;
import com.example.book_test_connection.repository.BookshelfRepository;
import com.example.book_test_connection.service.BookProgressService;
import com.example.book_test_connection.service.BookService;
import com.example.book_test_connection.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookProgressService bookProgressService;
    private final UserService userService;
    private final BookshelfRepository bookshelfRepository;

    public BookController(BookService bookService, BookProgressService bookProgressService, UserService userService, BookshelfRepository bookshelfRepository) {
        this.bookService = bookService;
        this.bookProgressService = bookProgressService;
        this.userService = userService;
        this.bookshelfRepository = bookshelfRepository;
    }

    // GET /api/books → получить все книги
    @GetMapping
    public List<Book> getAllBooks() {
        boolean isAdmin = isCurrentUserAdmin();
        List<Book> books = bookService.findAllBooks();
        for (Book book : books) {
            book.setCanDelete(isAdmin);
        }
        return books;
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
        if(!isCurrentUserAdmin()){
            throw new NotEnoughRightsException("У вас недостаточно прав");
        }
        bookshelfRepository.deleteBookFromAllShelves(id);
        bookService.deleteBook(id); //удаляем книку
        bookProgressService.deleteBookProgressForAll(id); //удаляем прогресс ее чтения
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

        BookCreateRequest request = new BookCreateRequest();
        request.setName(name);
        request.setAuthor(author);
        request.setDescription(description);

        if (file.isEmpty()) {
            Book savedBook = bookService.createBook(request);
            var location = uriBuilder.path("/api/books/{id}").buildAndExpand(savedBook.getId()).toUri();
            return ResponseEntity.created(location).body(savedBook);
        } else {
            Book savedBook = bookService.createBookWithFile(request, file);
            var location = uriBuilder.path("/api/books/{id}").buildAndExpand(savedBook.getId()).toUri();
            return ResponseEntity.created(location).body(savedBook);
        }
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


    // Получить прогресс чтения книги
    @GetMapping("/{bookId}/progress")
    public ResponseEntity<BookProgress> getBookProgress(@PathVariable Long bookId) {
        Long currentUserId = getCurrentUserId();
        BookProgress progress = bookProgressService.getOrCreateProgress(currentUserId, bookId);
        return ResponseEntity.ok(progress);
    }

    // Обновить прогресс чтения книги
    @PutMapping("/{bookId}/progress")
    public ResponseEntity<BookProgress> updateBookProgress(
            @PathVariable Long bookId,
            @Valid @RequestBody BookProgressUpdateRequest request
    ) {
        Long currentUserId = getCurrentUserId();
        BookProgress updated = bookProgressService.updateProgress(currentUserId, bookId, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/read")
    public ResponseEntity<String> readBook(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page
    ) {
        // 1. Получаем книгу
        Book book = bookService.findBookById(id);

        // 2. Проверяем наличие htmlPath
        if (book.getHtmlPath() == null || book.getHtmlPath().isEmpty()) {
            return ResponseEntity.badRequest().body("HTML version of the book is not available");
        }

        // 3. Читаем HTML-файл с диска
        Path htmlFile = Paths.get(book.getHtmlPath());
        if (!Files.exists(htmlFile)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("HTML file not found: " + book.getHtmlPath());
        }

        String html;
        try {
            html = Files.readString(htmlFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read HTML file");
        }

        // 5. Отдаём как HTML
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    //получение контента книги
    @GetMapping("/{id}/content")
    public ResponseEntity<String> getBookContent(@PathVariable Long id) {
        // Проверка доступа через SecurityContextHolder (JWT уже обработан фильтром)
        Book book = bookService.findBookById(id);
        if (book.getHtmlPath() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String html = Files.readString(Paths.get(book.getHtmlPath()), StandardCharsets.UTF_8);
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Вспомогательный метод: получает userId текущего пользователя (из jwt?)
    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return bookService.getUserIdByEmail(email); // делегируем сервису
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        return authentication.getName(); // это email, если вы используете email как username
    }

    private boolean isCurrentUserAdmin() {
        return userService.isUserAdmin(getCurrentUserId());
    }
}