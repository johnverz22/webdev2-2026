package com.johnverz.demo;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {
    @GetMapping("/books")
    public String showBooks(Model model) {

        Author rowling = new Author("J.K. Rowling");
        Author orwell = new Author("George Orwell");
        Author tolkien = new Author("J.R.R. Tolkien");

        List<Book> books = List.of(
            new Book("Harry Potter and the Philosopher's Stone", rowling),
            new Book("1984", orwell),
            new Book("The Hobbit", tolkien)
        );

        // Add the list to the Model using the key "books"
        model.addAttribute("books", books);

        // Maps to:
        // src/main/resources/templates/books.html
        return "books";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}