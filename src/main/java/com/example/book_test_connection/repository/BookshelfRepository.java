package com.example.book_test_connection.repository;

import com.example.book_test_connection.entity.Bookshelf;
import com.example.book_test_connection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    List<Bookshelf> findByUserId(Long userId);  //книжных полок у одного может быть несколько
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}