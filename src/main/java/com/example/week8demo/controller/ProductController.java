package com.example.week8demo.controller;

import com.example.week8demo.entity.Product;
import com.example.week8demo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web layer for products, server-rendered with Thymeleaf (Weeks 3-4 pattern).
 * Thin by design: each handler translates one HTTP request into service calls,
 * puts the result in the {@code Model}, and names a template. All business rules
 * and transactions live in {@link ProductService}.
 *
 * <p><b>Page map (open in a browser — see README):</b></p>
 * <pre>
 * GET  /                        redirect to the catalog (see HomeController)
 * GET  /products                  catalog list            (products.html)
 * GET  /products/search?keyword=  keyword search          (products.html, Topic 3 derived)
 * GET  /products/cheap?max=       price filter            (products.html, Topic 3 derived)
 * GET  /products/category?name=   category lookup         (products.html, Topic 3 @Query)
 * GET  /products/new              blank create form       (product-form.html)
 * POST /products                  create                  (redirect to list)
 * GET  /products/{id}             detail + sell form      (product-detail.html)
 * GET  /products/{id}/edit        pre-filled edit form    (product-form.html)
 * POST /products/{id}             update                  (redirect to detail)
 * POST /products/{id}/delete      remove                  (redirect to list)
 * POST /products/{id}/sell?qty=   stock decrement         (Topic 4, redirect to detail)
 * GET  /products/batch            two-product batch form  (batch-form.html, Topic 4 rollback demo)
 * POST /products/batch            atomic batch save       (redirect to list, or re-show form on clash)
 * </pre>
 *
 * <p>Form validation follows the Week 4 pattern: {@code @Valid} plus a
 * {@code BindingResult} placed immediately after the {@code @ModelAttribute}.
 * When {@code result.hasErrors()} is true the same form view returns so
 * {@code th:errors} can show messages next to the failing fields.</p>
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor injection of the service layer.
     *
     * @param productService business layer, provided by Spring
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ------------------------------------------------------------------
    // List + Topic 3 filtered views (all render products.html)
    // ------------------------------------------------------------------

    /**
     * Shows the full catalog.
     *
     * @param model receives {@code products} (all rows) and a page {@code heading}
     * @return the {@code products} template
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("heading", "All products");
        return "products";
    }

    /**
     * Keyword search (Topic 3 derived query {@code findByNameContainingIgnoreCase}).
     *
     * @param keyword substring to look for inside the name
     * @param model   receives the matches and a heading echoing the keyword
     * @return the {@code products} template
     */
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("products", productService.searchByName(keyword));
        model.addAttribute("heading", "Results for \"" + keyword + "\"");
        return "products";
    }

    /**
     * Price filter (Topic 3 derived query {@code findByPriceLessThan}).
     *
     * @param max exclusive price ceiling
     * @param model receives the matches and a heading echoing the ceiling
     * @return the {@code products} template
     */
    @GetMapping("/cheap")
    public String cheaperThan(@RequestParam Double max, Model model) {
        model.addAttribute("products", productService.cheaperThan(max));
        model.addAttribute("heading", "Cheaper than " + max);
        return "products";
    }

    /**
     * Category lookup (Topic 3 custom JPQL with {@code @Param}).
     *
     * @param name category label in any letter case
     * @param model receives the matches and a heading echoing the category
     * @return the {@code products} template
     */
    @GetMapping("/category")
    public String byCategory(@RequestParam String name, Model model) {
        model.addAttribute("products", productService.byCategory(name));
        model.addAttribute("heading", "Category: " + name);
        return "products";
    }

    // ------------------------------------------------------------------
    // Detail
    // ------------------------------------------------------------------

    /**
     * Shows one product plus its sell form. A missing id throws
     * {@code EntityNotFoundException}, rendered as the {@code error} page (404)
     * by {@code GlobalExceptionHandler}.
     *
     * @param id    path id
     * @param model receives the {@code product}
     * @return the {@code product-detail} template
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product-detail";
    }

    // ------------------------------------------------------------------
    // Create / update forms (Week 4 th:object + th:field + th:errors pattern)
    // ------------------------------------------------------------------

    /**
     * Shows a blank create form. The empty {@code Product} must be in the Model
     * <i>before</i> rendering, otherwise the form's {@code th:object} fails.
     *
     * @param model receives a new {@code product} backing object
     * @return the {@code product-form} template
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    /**
     * Handles the create form. Invalid input (blank name, negative price, text in
     * a number field) re-shows the form with field errors; valid input saves and
     * redirects to the list (redirect-after-POST, so refresh never re-submits).
     *
     * @param product  form-bound product, validated
     * @param result   binding/validation outcome, must follow the model attribute
     * @param redirect carries the one-shot success message across the redirect
     * @return the form view again on errors, else a redirect to {@code /products}
     */
    @PostMapping
    public String create(@Valid @ModelAttribute Product product, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "product-form";
        }
        Product saved = productService.createProduct(product);
        redirect.addFlashAttribute("message", "Saved \"" + saved.getName() + "\".");
        return "redirect:/products";
    }

    /**
     * Shows the edit form pre-filled with the current row.
     *
     * @param id    path id of the product to edit
     * @param model receives the existing {@code product} as backing object
     * @return the {@code product-form} template
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product-form";
    }

    /**
     * Handles the edit form (same validation pattern as create).
     *
     * @param id       path id of the product to change
     * @param product  form-bound replacement values, validated
     * @param result   binding/validation outcome
     * @param redirect carries the one-shot success message across the redirect
     * @return the form view again on errors, else a redirect to the detail page
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Product product,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "product-form";
        }
        productService.updateProduct(id, product);
        redirect.addFlashAttribute("message", "Updated \"" + product.getName() + "\".");
        return "redirect:/products/" + id;
    }

    /**
     * Deletes a product, then redirects to the list with a confirmation message.
     *
     * @param id       path id of the product to remove
     * @param redirect carries the one-shot message across the redirect
     * @return a redirect to {@code /products}
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        Product removed = productService.getProductById(id);
        productService.deleteProduct(id);
        redirect.addFlashAttribute("message", "Deleted \"" + removed.getName() + "\".");
        return "redirect:/products";
    }

    // ------------------------------------------------------------------
    // Topic 4 demos: sell (single-transaction write) + batch (rollback)
    // ------------------------------------------------------------------

    /**
     * Sells units of a product. Business failures (bad quantity, short stock)
     * re-show the detail page with an inline error instead of crashing.
     *
     * @param id    product to sell from
     * @param qty   units to remove
     * @param model receives the product plus a {@code sellError} message on failure
     * @param redirect carries the success message across the redirect
     * @return the detail view on failure, else a redirect to the detail page
     */
    @PostMapping("/{id}/sell")
    public String sell(@PathVariable Long id, @RequestParam int qty,
                       Model model, RedirectAttributes redirect) {
        try {
            Product updated = productService.sell(id, qty);
            redirect.addFlashAttribute("message", "Sold " + qty + ". Stock now " + updated.getStock() + ".");
            return "redirect:/products/" + id;
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            model.addAttribute("product", productService.getProductById(id));
            model.addAttribute("sellError", ex.getMessage());
            return "product-detail";
        }
    }

    /**
     * Shows the two-product batch form for the rollback experiment.
     *
     * @param model receives a blank {@code form} holder with two rows
     * @return the {@code batch-form} template
     */
    @GetMapping("/batch")
    public String showBatchForm(Model model) {
        BatchForm form = new BatchForm();
        form.getSecond().setName("Demo One");
        model.addAttribute("form", form);
        return "batch-form";
    }

    /**
     * Saves both rows atomically. When the second row clashes (for example both
     * rows share a name), the UNIQUE constraint fires, everything rolls back,
     * and the form re-shows with an explanation — <i>neither</i> row was saved.
     *
     * @param form     the two submitted rows, bound via {@code th:object}
     * @param model    receives a {@code batchError} message on constraint clash
     * @param redirect carries the success message across the redirect
     * @return the batch form again on constraint clash, else a redirect to the list
     */
    @PostMapping("/batch")
    public String batch(@ModelAttribute("form") BatchForm form,
                        Model model, RedirectAttributes redirect) {
        List<Product> batch = new ArrayList<>();
        batch.add(form.getFirst().toProduct());
        batch.add(form.getSecond().toProduct());
        try {
            productService.batchCreate(batch);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("batchError",
                    "Batch rejected and fully rolled back — neither row was saved. "
                    + "Check the console: the first INSERT printed, then was undone. "
                    + "The last \"Caused by:\" names the violated constraint.");
            return "batch-form";
        }
        redirect.addFlashAttribute("message", "Batch saved: 2 products.");
        return "redirect:/products";
    }

    /**
     * Holder for the two rows of the batch form. One wrapper object (instead of
     * two separate model attributes) lets the template bind nested paths like
     * {@code *{first.name}} with a single {@code th:object}.
     */
    public static class BatchForm {

        private final BatchRow first = new BatchRow();
        private final BatchRow second = new BatchRow();

        /**
         * Returns the holder for row one.
         *
         * @return first row, never {@code null}
         */
        public BatchRow getFirst() {
            return first;
        }

        /**
         * Returns the holder for row two.
         *
         * @return second row, never {@code null}
         */
        public BatchRow getSecond() {
            return second;
        }
    }

    /**
     * Plain holder for one row of the batch form. A dedicated holder (instead of
     * binding a {@code List<Product>} directly) keeps the form markup readable for
     * students meeting multi-row forms for the first time.
     */
    public static class BatchRow {

        private String name;
        private Double price;
        private String category;
        private Integer stock;

        /**
         * Returns the entered product name.
         *
         * @return row name, may be blank (then the batch fails loudly — on purpose)
         */
        public String getName() {
            return name;
        }

        /**
         * @param name row name from the form
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns the entered price.
         *
         * @return row price, may be {@code null}
         */
        public Double getPrice() {
            return price;
        }

        /**
         * @param price row price from the form
         */
        public void setPrice(Double price) {
            this.price = price;
        }

        /**
         * Returns the entered category.
         *
         * @return row category, may be {@code null}
         */
        public String getCategory() {
            return category;
        }

        /**
         * @param category row category from the form
         */
        public void setCategory(String category) {
            this.category = category;
        }

        /**
         * Returns the entered stock.
         *
         * @return row stock, may be {@code null} (treated as 0)
         */
        public Integer getStock() {
            return stock;
        }

        /**
         * @param stock row stock from the form
         */
        public void setStock(Integer stock) {
            this.stock = stock;
        }

        /**
         * Converts this row into a new transient product for saving.
         *
         * @return a product with {@code null} id (INSERT path)
         */
        public Product toProduct() {
            return new Product(name, price, category, stock == null ? 0 : stock);
        }
    }
}
