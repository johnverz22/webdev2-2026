package com.johnverz.bookmanager.repository;

import org.springframework.stereotype.Repository;

import com.johnverz.bookmanager.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TOPIC 1: Controller-Service-Repository Architecture (the Repository layer)
 *
 * IMPORTANT: This is a plain in-memory repository, NOT Spring Data JPA.
 * Spring Data JPA and real databases aren't covered until Week 7-8. This
 * class just behaves like a repository would - findAll(), findById(),
 * save() - while actually storing everything in a Map that lives only as
 * long as the application is running (restart the app, the data resets).
 *
 * Notice what this class does NOT know anything about: HTTP, controllers,
 * views, or validation. It only knows how to store and retrieve Book
 * objects. That's the entire responsibility of a repository.
 */
@Repository
public class BookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(idGenerator.incrementAndGet());
        }
        storage.put(book.getId(), book);
        return book;
    }
}
