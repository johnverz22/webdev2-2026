package com.johnverz.bookmanager.service;

import org.springframework.stereotype.Service;

import com.johnverz.bookmanager.dto.BookSummaryDto;
import com.johnverz.bookmanager.exception.BookNotFoundException;
import com.johnverz.bookmanager.model.Book;
import com.johnverz.bookmanager.repository.BookRepository;

import java.util.List;

/**
 * TOPIC 1: Controller-Service-Repository Architecture (the Service layer)
 * TOPIC 2: DTO conversion happens here, in exactly one place
 * TOPIC 4: This is where a BookNotFoundException actually gets thrown
 *
 * BookService owns the decision-making: what counts as a valid book,
 * what to do when a lookup fails, and how to shape data for a given
 * purpose (see getSummary). It knows nothing about HTTP - no requests,
 * no responses, no view names. That's the Controller's job.
 */
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    /**
     * Looks up a book by id, or throws BookNotFoundException if it
     * doesn't exist. The Controller doesn't check for null anywhere -
     * it just calls this and lets the exception (if any) be handled
     * by an @ExceptionHandler.
     */
    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("No book with id " + id));
    }

    /**
     * TOPIC 2: Converts a full Book (repository object) into a
     * BookSummaryDto (view object) containing only what a summary
     * page needs. This is the ONE place that conversion happens.
     */
    public BookSummaryDto getSummary(Long id) {
        Book book = findById(id);
        return new BookSummaryDto(book.getTitle(), book.getAuthor());
    }

    /**
     * TOPIC 5: This is the extracted save logic that used to sit
     * directly inside the controller's create() method. The
     * controller now just delegates to this one line.
     */
    public void createBook(Book book) {
        repository.save(book);
    }
}
