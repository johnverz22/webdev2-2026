package com.johnverz.bookmanager.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.johnverz.bookmanager.exception.BookNotFoundException;
import com.johnverz.bookmanager.model.Book;
import com.johnverz.bookmanager.service.BookService;

import org.springframework.validation.BindingResult;

/**
 * TOPIC 1 + TOPIC 5: This is the "after" controller - compare its size
 * and shape to the tangled, do-everything version shown in the "Before"
 * slides. Every method here does ONE thing: read the request, ask the
 * service for a decision or data, and pick a response. No business
 * rules, no data access, and (almost) no error-handling logic live here.
 *
 * TOPIC 3: @Valid on the @ModelAttribute parameter of create() is what
 * actually switches on the @NotBlank checks declared on Book. Remove
 * @Valid and those annotations still compile fine - they just silently
 * stop being checked. Try removing it yourself and watch what happens.
 *
 * TOPIC 4: handleNotFound() below only catches BookNotFoundException
 * thrown by THIS controller's own methods. See GlobalExceptionHandler
 * for the broader, application-wide safety net.
 */
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", service.findAll());
        return "books/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Book book, BindingResult result) {
        if (result.hasErrors()) {
            // @Valid already populated `result` with any @NotBlank
            // violations - no manual `if (title == null)` checks needed.
            return "books/form";
        }
        service.createBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // No null check here on purpose: if the book doesn't exist,
        // service.findById() throws BookNotFoundException, and Spring
        // routes execution to handleNotFound() below instead of this
        // method continuing.
        Book book = service.findById(id);
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/{id}/summary")
    public String summary(@PathVariable Long id, Model model) {
        // TOPIC 2 in action: the view only ever receives a
        // BookSummaryDto here, never the full Book.
        model.addAttribute("summary", service.getSummary(id));
        return "books/summary";
    }

    @ExceptionHandler(BookNotFoundException.class)
    public String handleNotFound(BookNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
