package com.example.book_test_connection.service;

import com.example.book_test_connection.dto.BookCreateRequest;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.exceptions.BookNotFoundException;
import com.example.book_test_connection.exceptions.UploadErrorException;
import com.example.book_test_connection.repository.BookRepository;
import com.example.book_test_connection.utils.FileValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    @Value("${app.upload.dir:./uploads/books}")
    private String uploadDir;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll(); // JpaRepository возвращает List — всё ок
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    // Принимает DTO, а не сущность
    public Book createBook(BookCreateRequest request) {
        Book book = new Book(request.getName(), request.getAuthor(), request.getDescription());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        Book book = bookRepository.findBookById(id);
        if (book.getFilePath() != null && !book.getFilePath().isBlank()) {
            try {
                Path filePath = Paths.get(book.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Файл книги удалён: {}", filePath);
                }
            } catch (IOException e) {
                // Логируем ошибку, но не прерываем удаление из БД
                log.warn("Не удалось удалить файл книги: {}", book.getFilePath(), e);
            }
        }
        bookRepository.deleteById(id);
        log.info("Книга удалена");
    }
    // В BookService.java

    public Book updateBook(Long id, BookCreateRequest request) {
        // 1. Находим существующую книгу
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // 2. Обновляем поля
        existingBook.setName(request.getName());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setDescription(request.getDescription());

        // 3. Сохраняем и возвращаем
        return bookRepository.save(existingBook);
    }

    public Book createBookWithFile(BookCreateRequest request, MultipartFile file) {
        try {
            // Создаём директорию, если её нет
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            //проверка формата файла
            if (!FileValidationUtils.hasAllowedExtension(file)) {
                throw new UploadErrorException("Invalid file format. Use the following formats: " + FileValidationUtils.getAllowedExtensionsAsString());
            }

            // Генерируем уникальное имя файла (чтобы избежать коллизий)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Сохраняем файл на диск
            Files.copy(file.getInputStream(), filePath);

            // Создаём книгу
            Book book = new Book(request.getName(), request.getAuthor(), request.getDescription());
            book.setFilePath(filePath.toString()); // или relative path: "./uploads/books/" + uniqueFilename

            return bookRepository.save(book);

        } catch (IOException e) {
            throw new UploadErrorException("Unable to save book, try again later...");
        }
    }

    //прикрепить файл к книге без файла
    public Book attachFileToBook(Long id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadErrorException("File cannot be empty");
        }
        if (!FileValidationUtils.hasAllowedExtension(file)) {
            throw new UploadErrorException("Invalid file format. Use the following formats: " + FileValidationUtils.getAllowedExtensionsAsString());
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        //если файл уже есть — запрещаем замену
        if (book.getFilePath() != null && !book.getFilePath().isBlank()) {
            throw new UploadErrorException("This book already have file. You cannot change it");
        }

        try {
            // Создаём директорию, если её нет
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(uniqueFilename);

            // Сохраняем файл
            Files.copy(file.getInputStream(), targetPath);

            // Привязываем путь к книге
            book.setFilePath(targetPath.toString());
            return bookRepository.save(book);

        } catch (IOException e) {
            log.warn("Cant attach file to book with id " + id + " " + e.getMessage());
            throw new UploadErrorException("Не удалось сохранить файл");

        }
    }
}