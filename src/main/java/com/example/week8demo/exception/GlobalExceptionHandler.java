package com.example.week8demo.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Renders friendly error <i>pages</i> (not JSON — this course has not done REST
 * yet) for persistence and validation failures (Topic 5 — <i>don't leak stack
 * traces to users</i>).
 *
 * <p><b>How to read the mapping:</b></p>
 * <ul>
 *   <li>Form validation fails <i>before</i> SQL ({@code @Valid} + {@code BindingResult}
 *       in the controller) → the form re-renders with {@code th:errors}. It never
 *       reaches this class — compare with the inline handling in
 *       {@code ProductController#create}.</li>
 *   <li>The database rejects an INSERT/UPDATE (UNIQUE, NOT NULL, foreign key)
 *       → {@code DataIntegrityViolationException} → the {@code error} page with a
 *       plain-words message. Tip: scroll to the last {@code Caused by:} in the
 *       console — that line names the real constraint.</li>
 *   <li>A lookup finds nothing → {@code EntityNotFoundException} → the
 *       {@code error} page as a 404. Distinct from validation errors: the query ran
 *       fine, the row just is absent.</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Renders "row not found" failures from {@code orElseThrow} lookups.
     *
     * @param ex    the missing-entity failure (its message already has the id)
     * @param model receives the page's {@code status}, {@code title} and {@code message}
     * @param response used to set the real HTTP status to 404
     * @return the {@code error} template
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("status", 404);
        model.addAttribute("title", "Not found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("hint", "The query ran fine — the row just does not exist. "
                + "Go back to the list and pick an id you can see there.");
        return "error";
    }

    /**
     * Renders database rejections (duplicate unique name, null in a NOT NULL
     * column, ...). The page message stays generic on purpose — the detailed
     * constraint name stays in the server log, not in the user's browser.
     *
     * @param ex    the Spring translation of the JDBC/Hibernate failure
     * @param model receives the page's {@code status}, {@code title} and {@code message}
     * @param response used to set the real HTTP status to 409
     * @return the {@code error} template
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleConflict(DataIntegrityViolationException ex, Model model,
                                 HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        model.addAttribute("status", 409);
        model.addAttribute("title", "Database rejected the write");
        model.addAttribute("message", "Duplicate or invalid value — the database said no.");
        model.addAttribute("hint", "Open the console and scroll to the last \"Caused by:\" — "
                + "that line names the violated constraint (try submitting a duplicate name).");
        return "error";
    }

    /**
     * Renders direct Bean Validation failures (service-layer validation, if added).
     * Form-body failures are handled inline via {@code BindingResult} instead and
     * never reach here.
     *
     * @param ex    the constraint failure
     * @param model receives the page's {@code status}, {@code title} and {@code message}
     * @param response used to set the real HTTP status to 400
     * @return the {@code error} template
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidation(ConstraintViolationException ex, Model model,
                                   HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute("status", 400);
        model.addAttribute("title", "Invalid input");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("hint", "Fix the highlighted fields and submit again.");
        return "error";
    }

    /**
     * Renders bad arguments such as a non-positive sell quantity.
     *
     * @param ex    the illegal argument or state (its message explains the rule)
     * @param model receives the page's {@code status}, {@code title} and {@code message}
     * @param response used to set the real HTTP status to 400
     * @return the {@code error} template
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBadRequest(RuntimeException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute("status", 400);
        model.addAttribute("title", "Could not do that");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("hint", "Use the back link and try values inside the allowed range.");
        return "error";
    }
}
