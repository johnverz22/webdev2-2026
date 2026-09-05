package com.johnverz.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @GetMapping("/book")
    public String showBook(Model model) {

        Author author = new Author(
            "J.K. Rowling"
        );

        Book book = new Book(
            "Harry Potter and the Philosopher's Stone",
            author
        );

        // Add the Book object to the Model using the key "book"
        model.addAttribute("book", book);

        // "book-detail" maps to:
        // src/main/resources/templates/book-detail.html
        return "book-detail";
    }
}
