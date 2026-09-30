package com.example.week8demo.config;

import com.example.week8demo.entity.Category;
import com.example.week8demo.entity.Product;
import com.example.week8demo.repository.CategoryRepository;
import com.example.week8demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a tiny catalog on every boot so every endpoint returns data immediately.
 *
 * <p>Runs once at startup, after the Spring context is ready. Uses
 * {@code existsByName} guards so restarts do not insert duplicates — with
 * {@code ddl-auto=update} the MySQL tables (and their rows) survive restarts,
 * so seeding must be idempotent.</p>
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository products;
    private final CategoryRepository categories;

    /**
     * Creates the seeder with its required repositories.
     *
     * @param products   product repository used for seeding and duplicate checks
     * @param categories category repository used for seeding
     */
    public DataSeeder(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    /**
     * Inserts the sample categories and products unless they already exist.
     *
     * @param args ignored
     */
    @Override
    public void run(String... args) {
        categories.findByName("Beverages").orElseGet(() -> categories.save(new Category("Beverages")));
        categories.findByName("Stationery").orElseGet(() -> categories.save(new Category("Stationery")));

        seed("Espresso Beans", 12.50, "Beverages", 100);
        seed("Green Tea", 4.75, "Beverages", 60);
        seed("Ballpoint Pen", 1.20, "Stationery", 500);
        seed("Notebook A5", 3.40, "Stationery", 200);
    }

    private void seed(String name, double price, String category, int stock) {
        if (!products.existsByName(name)) {
            products.save(new Product(name, price, category, stock));
        }
    }
}
