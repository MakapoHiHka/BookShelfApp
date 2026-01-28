package com.example.book_test_connection.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "book_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"})
)
public class BookProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "page_number")
    private Integer pageNumber;

    protected BookProgress(){};

    public BookProgress(long userId, long bookId, Integer pageNumber){
        this.userId = userId;
        this.bookId = bookId;
        this.pageNumber = pageNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }


    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}