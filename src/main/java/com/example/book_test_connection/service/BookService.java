package com.example.book_test_connection.service;

import com.example.book_test_connection.dto.BookCreateRequest;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll(); // JpaRepository возвращает List — всё ок
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    // Принимает DTO, а не сущность
    public Book createBook(BookCreateRequest request) {
        Book book = new Book(request.getName(), request.getAuthor(), request.getDescription());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    // В BookService.java

    public Book updateBook(Long id, BookCreateRequest request) {
        // 1. Находим существующую книгу
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // 2. Обновляем поля
        existingBook.setName(request.getName());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setDescription(request.getDescription());

        // 3. Сохраняем и возвращаем
        return bookRepository.save(existingBook);
    }
}