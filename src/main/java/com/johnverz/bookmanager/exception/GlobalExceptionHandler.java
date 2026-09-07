package com.johnverz.bookmanager.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * TOPIC 4: Application Error Handling - the OTHER level of handling.
 *
 * BookController has its own local @ExceptionHandler for
 * BookNotFoundException (see BookController#handleNotFound) - that one
 * only catches exceptions thrown by BookController's own methods.
 *
 * @ControllerAdvice is broader: this class applies across EVERY
 * controller in the application, not just one. Here it acts as a safety
 * net that catches anything unexpected we didn't specifically plan for,
 * so a real user never sees a raw stack trace.
 *
 * If this project grew a second controller that could also throw
 * BookNotFoundException, the "Watch Out" advice from the slides applies:
 * move that specific handler here too, instead of repeating it in every
 * controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAnyOtherException(Exception ex, Model model) {
        model.addAttribute("message",
                "Something went wrong on our end. Please try again.");
        return "error";
    }
}
