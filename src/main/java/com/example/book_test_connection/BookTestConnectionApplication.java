package com.example.book_test_connection;

import com.example.book_test_connection.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookTestConnectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookTestConnectionApplication.class, args);
	}
}
