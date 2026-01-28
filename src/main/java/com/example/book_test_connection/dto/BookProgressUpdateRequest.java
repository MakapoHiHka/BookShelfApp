package com.example.book_test_connection.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookProgressUpdateRequest {

    @NotNull(message = "Номер страницы обязателен")
    @Min(value = 0, message = "Номер страницы не может быть отрицательным")
    private Integer pageNumber;

    public Integer getPageNumber(){ return pageNumber; }
    public void setPageNumber(Integer pageNumber){ this.pageNumber = pageNumber; }

}