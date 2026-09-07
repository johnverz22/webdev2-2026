package com.johnverz.bookmanager.model;

import jakarta.validation.constraints.NotBlank;

/**
 * TOPIC 3: Bean Validation & Custom Validation Messages
 *
 * These annotations describe what a "valid" Book looks like, right where
 * the fields live. Nothing here checks anything by itself - the checking
 * only happens where a controller method adds @Valid in front of a
 * @ModelAttribute Book parameter (see BookController#create).
 *
 * The `message` attribute on each constraint is what th:errors displays
 * on the form (see templates/books/form.html).
 */
public class Book {

    private Long id;

    @NotBlank(message = "Please enter a book title.")
    private String title;

    @NotBlank(message = "Please enter the author's name.")
    private String author;

    public Book() {
        // Spring needs a no-argument constructor to bind form data onto this object.
    }

    public Book(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
