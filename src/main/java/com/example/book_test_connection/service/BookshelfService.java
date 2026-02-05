package com.example.book_test_connection.service;

import com.example.book_test_connection.entity.Bookshelf;
import com.example.book_test_connection.entity.User;
import com.example.book_test_connection.exceptions.BookNotFoundException;
import com.example.book_test_connection.exceptions.NotEnoughRightsException;
import com.example.book_test_connection.exceptions.ShelfNotFoundException;
import com.example.book_test_connection.repository.BookRepository;
import com.example.book_test_connection.repository.BookshelfRepository;
import com.example.book_test_connection.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookshelfService(BookshelfRepository bookshelfRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository) {
        this.bookshelfRepository = bookshelfRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<Bookshelf> findByUserId(Long userId) {
        return bookshelfRepository.findByUserId(userId);
    }

    public Bookshelf createBookshelf(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bookshelf shelf = new Bookshelf(name, user);
        return bookshelfRepository.save(shelf);
    }

    public void addBookToShelf(Long shelfId, Long bookId, Long currentUserId) {
        // Проверяем существование книги
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found with id: " + bookId);
        }

        Bookshelf shelf = bookshelfRepository.findById(shelfId)
                .orElseThrow(() -> new ShelfNotFoundException("Bookshelf not found"));

        // Проверка владения
        if (!shelf.getUser().getId().equals(currentUserId)) {
            throw new NotEnoughRightsException("You do not own this bookshelf");
        }
        shelf.addBook(bookRepository.findBookById(bookId));
        bookshelfRepository.save(shelf);
    }

    public void removeBookFromShelf(Long shelfId, Long bookId, Long currentUserId) {
        Bookshelf shelf = bookshelfRepository.findById(shelfId)
                .orElseThrow(() -> new ShelfNotFoundException("Bookshelf not found"));

        if (!shelf.getUser().getId().equals(currentUserId)) {
            throw new NotEnoughRightsException("You do not own this bookshelf");
        }

        // Проверяем наличие книги по ID
        boolean bookExistsInShelf = shelf.getBooks().stream()
                .anyMatch(book -> book.getId().equals(bookId));

        if (!bookExistsInShelf) {
            throw new BookNotFoundException("Book with " + bookId + " id is not in this bookshelf");
        }
        // Удаляем по ID
        shelf.getBooks().removeIf(book -> book.getId().equals(bookId));
        bookshelfRepository.save(shelf);
    }

    public void deleteBookshelf(Long shelfId, Long currentUserId) {
        Bookshelf shelf = bookshelfRepository.findById(shelfId)
                .orElseThrow(() -> new BookNotFoundException("Bookshelf not found"));

        if (!shelf.getUser().getId().equals(currentUserId)) {
            throw new NotEnoughRightsException("You do not own this bookshelf");
        }
        bookshelfRepository.delete(shelf);
    }
}