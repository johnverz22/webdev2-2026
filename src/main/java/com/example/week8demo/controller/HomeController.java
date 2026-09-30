package com.example.week8demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sends the site root to the catalog. Without this, opening
 * {@code http://localhost:8080/} matches no handler and Spring answers 404 —
 * a confusing first impression when the app is actually running fine.
 */
@Controller
public class HomeController {

    /**
     * Redirects the root URL to the product catalog.
     *
     * @return a redirect to {@code /products}
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }
}
