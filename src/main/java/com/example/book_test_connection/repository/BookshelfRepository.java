package com.example.book_test_connection.repository;

import com.example.book_test_connection.entity.Bookshelf;
import com.example.book_test_connection.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    List<Bookshelf> findByUserId(Long userId);  //книжных полок у одного может быть несколько
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM bookshelf_books WHERE book_id = :bookId", nativeQuery = true)
    void deleteBookFromAllShelves(@Param("bookId") Long bookId);
}