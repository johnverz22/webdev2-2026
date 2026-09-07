package com.johnverz.bookmanager.dto;

/**
 * TOPIC 2: Separation of Concerns & DTO Introduction
 *
 * This is NOT the same object the repository stores. It holds only
 * what a "summary" view actually needs to display: a title and an
 * author name - nothing about IDs, nothing about how a Book is stored.
 *
 * If we later added a field to Book that's for internal use only
 * (say, an internal notes field, or an audit timestamp), it would
 * NOT automatically show up wherever a BookSummaryDto is used to
 * render a page - because this class simply never declares it.
 * That's the entire point of a DTO.
 */
public class BookSummaryDto {

    private final String title;
    private final String authorName;

    public BookSummaryDto(String title, String authorName) {
        this.title = title;
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }
}
