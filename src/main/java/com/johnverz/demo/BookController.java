package com.johnverz.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {
    //Temporary in-memory database
    private static final List<Book> books = new ArrayList<>();

    @GetMapping("/books/new")
    public String showBookForm(Model model) {

        // Create an empty Book for the form to bind to.
        model.addAttribute("book", new Book());

        return "book-form";
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute Book book) {

        //For now, save the submitted Book 
        //into our temporary in-memory List.
        books.add(book);

        // Post/Redirect/Get
        return "redirect:/books";
    }


    @GetMapping("/books")
    public String showBooks(Model model) {

        // For now, retrieve books from our in-memory List.
        model.addAttribute("books", books);

        return "books";
    }
}