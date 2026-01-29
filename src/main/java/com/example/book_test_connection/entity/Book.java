package com.example.book_test_connection.entity;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private String author;
    private String description;
    private String filePath;
    private String htmlPath;
    private Integer totalPage;

    protected Book() {}

    public Book(String Name, String Author, String description) {
        this.name = Name;
        this.author = Author;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "Book[id=%d, name='%s', author='%s', description='%s']",
                id, name, author, description);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath(){ return filePath; }
    public void setFilePath(String filePath){ this.filePath = filePath; }

    public String getHtmlPath(){ return htmlPath; }
    public void setHtmlPath(String path){ this.htmlPath = path; }

    public void setTotalPage(int totalPage){ this.totalPage = totalPage; System.out.println(12);}
    public Integer getTotalPage() { return totalPage; }
}