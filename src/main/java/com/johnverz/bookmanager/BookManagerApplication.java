package com.johnverz.bookmanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.johnverz.bookmanager.model.Book;
import com.johnverz.bookmanager.repository.BookRepository;

/**
 * WEBDEV2 - Week 5 Demo Application.
 *
 * This app is intentionally small: ONE feature (managing books), built
 * three different ways across the course of the week's slides:
 *
 *   Topic 1 - Controller -> Service -> Repository layering
 *   Topic 2 - DTOs (BookSummaryDto) so the view never sees more than it needs
 *   Topic 3 - Bean Validation (@NotBlank, @Valid) instead of hand-written ifs
 *   Topic 4 - Custom exceptions + @ExceptionHandler / @ControllerAdvice
 *   Topic 5 - The whole thing, refactored, is what you're looking at right now
 *
 * NOTE: We do NOT use a real database or JPA here. Spring Data JPA isn't
 * taught until Week 7, so BookRepository below is a plain in-memory class
 * that behaves like a repository (findAll/findById/save) without a database
 * underneath it. Swapping it for a real JPA repository in Week 7 will not
 * require touching the Controller or Service at all - that's the whole
 * point of layering.
 */
@SpringBootApplication
public class BookManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagerApplication.class, args);
    }

    /**
     * Seeds a couple of books on startup purely so the list page isn't
     * empty the first time you open the app. Nothing about this is a
     * required Spring feature - CommandLineRunner just runs once after
     * the application context has started.
     */
    @Bean
    CommandLineRunner seedData(BookRepository repository) {
        return args -> {
            repository.save(new Book(null, "Clean Code", "Robert C. Martin"));
            repository.save(new Book(null, "Effective Java", "Joshua Bloch"));
        };
    }
}
