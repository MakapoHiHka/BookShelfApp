package com.example.book_test_connection.repository;

import com.example.book_test_connection.entity.BookProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookProgressRepository extends JpaRepository<BookProgress, Long> {
    Optional<BookProgress> findByUserIdAndBookId(Long userId, Long bookId);
    List<BookProgress> findByUserId(Long userId);
    void deleteByBookId(Long bookId);
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByBookId(Long bookId);
}