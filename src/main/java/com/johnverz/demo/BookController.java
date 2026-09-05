package com.johnverz.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {

    /*
     * Temporary in-memory storage.
     *
     * This is only for our demonstration.
     * The records will be lost when the application stops.
     *
     * Later, this List will be replaced by a database,
     * typically accessed through a Repository.
     */
    private static final List<Book> books = new ArrayList<>();

    @GetMapping("/books/new")
    public String showBookForm(Model model) {

        // Empty Book for the form to bind to.
        model.addAttribute("book", new Book());

        return "book-form";
    }

    @PostMapping("/books")
    public String createBook(
            @ModelAttribute Book book,
            BindingResult result) {

        // Check whether Spring encountered
        // any binding errors.
        if (result.hasErrors()) {

            // Redisplay the form with the entered values
            // and the binding error messages.
            return "book-form";
        }

        // No binding errors, so save the Book.
        books.add(book);

        // Post/Redirect/Get
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String showBooks(Model model) {

        model.addAttribute("books", books);

        return "books";
    }
}