package com.example.book_test_connection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookTestConnectionApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookTestConnectionApplication.class, args);
	}
}
//при обновлении прогресса чтении книги узнает айди пользователя по bearer токену


