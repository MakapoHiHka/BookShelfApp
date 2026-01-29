package com.example.book_test_connection.dto;

public class ConvertationResult {
    private String html;
    private Integer totalPages;
    public ConvertationResult(String html, Integer totalPages){
        this.html = html;
        this.totalPages = totalPages;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
