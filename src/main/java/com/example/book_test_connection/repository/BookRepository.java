package com.example.book_test_connection.repository;

import com.example.book_test_connection.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(String author);  // поиск по автору
    Book findBookById(long id);
}