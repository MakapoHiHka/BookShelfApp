package com.example.book_test_connection.dto;

import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.entity.Bookshelf;

import java.util.Set;

public class BookshelfDto {
    private Long id;
    private String name;
    private Set<Book> books; // или List<Long>

    public BookshelfDto(Bookshelf shelf) {
        this.id = shelf.getId();
        this.name = shelf.getName();
        this.books = shelf.getBooks(); // твоё Set<Long>
    }

    // getters (без сеттеров, если immutable)
    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<Book> getBookIds() { return books; }
}