package com.example.book_test_connection.service;


import com.example.book_test_connection.dto.BookProgressUpdateRequest;
import com.example.book_test_connection.entity.BookProgress;
import com.example.book_test_connection.exceptions.BookNotFoundException;
import com.example.book_test_connection.repository.BookProgressRepository;
import com.example.book_test_connection.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class BookProgressService {

    private final BookProgressRepository bookProgressRepository;
    private final BookRepository bookRepository;

    public BookProgressService(BookProgressRepository bookProgressRepository, BookRepository bookRepository) {
        this.bookProgressRepository = bookProgressRepository;
        this.bookRepository = bookRepository;
    }

    //создает или обновляет прогресс
    @Transactional
    public BookProgress updateProgress(Long userId, Long bookId, BookProgressUpdateRequest request) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Книга не найдена: " + bookId);
        }
        // Находим существующий прогресс или создаём новый
        BookProgress progress = bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElse(new BookProgress(userId, bookId, null));

        progress.setPageNumber(request.getPageNumber());
        return bookProgressRepository.save(progress);
    }

    public Optional<BookProgress> getProgress(Long userId, Long bookId) {
        return bookProgressRepository.findByUserIdAndBookId(userId, bookId);
    }

    //если прогресса нет, то делает 0
    @Transactional
    public BookProgress getOrCreateProgress(Long userId, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Книга не найдена: " + bookId);
        }
        return bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> {
                    BookProgress newProgress = new BookProgress(userId, bookId, 0);
                    return bookProgressRepository.save(newProgress);
                });
    }
}
