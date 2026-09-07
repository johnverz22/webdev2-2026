package com.johnverz.bookmanager.exception;

/**
 * TOPIC 4: Application Error Handling
 *
 * A custom exception gives a specific, meaningful name to a specific
 * failure - "the book wasn't found" - instead of relying on a generic
 * exception type like RuntimeException everywhere. Anyone reading code
 * that throws or catches this instantly knows what went wrong.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }
}
