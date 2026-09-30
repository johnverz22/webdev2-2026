package com.example.week8demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * A sellable item in the demo catalog.
 *
 * <p><b>Week 7 recap (entity mapping):</b> {@code @Entity} marks this class as a JPA
 * entity, {@code @Id} plus {@code @GeneratedValue} declare the auto-generated primary
 * key, and {@code @Column} customises how fields map to table columns.</p>
 *
 * <p><b>Week 8 relevance:</b></p>
 * <ul>
 *   <li>{@code @Column(unique = true, nullable = false)} on {@code name} is what makes
 *       the Topic 4 rollback demo work — inserting two products with the same name
 *       violates this constraint and triggers a rollback.</li>
 *   <li>Bean Validation annotations ({@code @NotBlank}, {@code @Size},
 *       {@code @PositiveOrZero}) fire <i>before</i> SQL is sent (Topic 5:
 *       {@code ConstraintViolationException} vs {@code DataIntegrityViolationException}).</li>
 * </ul>
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Surrogate primary key. Never set this by hand on new entities — leave it
     * {@code null} and let {@code @GeneratedValue} assign it. Setting it manually
     * makes {@code save()} take the "merge" (UPDATE) path instead of "persist"
     * (INSERT), which is the classic Topic 2 gotcha.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business key of the product. Unique + not-null so duplicate inserts fail at
     * the database level — exactly what {@code batchCreate()} in
     * {@code ProductService} exploits to demonstrate transaction rollback.
     */
    @NotBlank(message = "Product name must not be blank")
    @Size(max = 120, message = "Product name must be at most 120 characters")
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    /** Selling price. Must be zero or positive; may be null (unknown price). */
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    /** Free-text grouping used by the derived-query and JPQL demos. */
    @Size(max = 80, message = "Category must be at most 80 characters")
    @Column(length = 80)
    private String category;

    /** Whether the product is currently offered. Used by active-only queries. */
    @Column(nullable = false)
    private boolean active = true;

    /** Units on hand. Used by the stock-decrement transaction demo. */
    @PositiveOrZero(message = "Stock must be zero or positive")
    @Column(nullable = false)
    private int stock = 0;

    /**
     * Optional link to a {@link Category} row. Many products may share one category.
     * Kept {@code ManyToOne} (not bidirectional) on purpose so the mapping stays
     * small enough to read in one sitting.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    private Category categoryRef;

    /**
     * No-argument constructor. Required by JPA and used as the blank backing
     * object for the create form — prefer the convenience constructor in your
     * own code so new products always start with real values.
     */
    public Product() {
    }

    /**
     * Creates a new (transient) product. The {@code id} stays {@code null} until
     * the entity is saved, which is how {@code save()} knows to INSERT.
     *
     * @param name     unique, non-blank product name
     * @param price    selling price, zero or positive
     * @param category free-text category label, may be {@code null}
     * @param stock    units on hand, zero or positive
     */
    public Product(String name, Double price, String category, int stock) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.active = true;
    }

    /**
     * Returns the generated primary key.
     *
     * @return the generated primary key, or {@code null} before first save
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the unique product name.
     *
     * @return the unique product name
     */
    public String getName() {
        return name;
    }

    /**
     * Renames the product (must stay unique or the next save fails with 409).
     *
     * @param name new non-blank name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the selling price.
     *
     * @return the selling price, possibly {@code null} when unknown
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price new selling price, zero or positive
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Returns the free-text category label.
     *
     * @return the free-text category label
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category new category label
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Tells whether the product is currently offered.
     *
     * @return {@code true} when the product is currently offered
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active {@code false} hides the product from active-only queries
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the units currently on hand.
     *
     * @return units currently on hand
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock new stock level, zero or positive
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Returns the linked category row.
     *
     * @return the linked category row, possibly {@code null}
     */
    public Category getCategoryRef() {
        return categoryRef;
    }

    /**
     * @param categoryRef category row to link, or {@code null} to unlink
     */
    public void setCategoryRef(Category categoryRef) {
        this.categoryRef = categoryRef;
    }
}
