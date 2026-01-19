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
	@Bean
	public CommandLineRunner demo(BookRepository repository) {
		return (args) -> {
			// save a few customers
//			repository.save(new Book("book1", "author1"));
//			repository.save(new Book("book2", "author2"));
//			repository.save(new Book("book3", "author2"));
//			repository.save(new Book("book4", "author3"));
//			repository.save(new Book("book6", "author4"));

//			System.out.println("Books found with findAll():");
//			repository.findAll().forEach(book -> {
//				System.out.println(book.toString());
//			});
//			System.out.println("");

//			// fetch an individual customer by ID
//			repository.findBookById(3L).toString();

//			logger.info("Customer found with findById(3L):");
//			logger.info("--------------------------------");
//			logger.info(customer.toString());
//			logger.info("");
//
//			// fetch customers by last name
//			logger.info("Customer found with findByLastName('Bauer'):");
//			logger.info("--------------------------------------------");
//			repository.findByLastName("Bauer").forEach(bauer -> {
//				logger.info(bauer.toString());
//			});
//			logger.info("");
		};
	}
}
