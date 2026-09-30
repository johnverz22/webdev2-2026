package com.example.week8demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A product grouping (for example "Beverages" or "Stationery").
 *
 * <p>Kept deliberately tiny: it exists so the demo has <b>two</b> repositories
 * (Topic 1: one proxy per repository interface) and so students can practise the
 * "declare a second repository and watch the app still boot" exercise from the
 * Week 8 handout.</p>
 */
@Entity
@Table(name = "categories")
public class Category {

    /** Auto-generated primary key; leave {@code null} on new instances. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Category label, unique so lookups by name are deterministic. */
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 80, message = "Category name must be at most 80 characters")
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    /** Required by JPA. */
    protected Category() {
    }

    /**
     * Creates a new (transient) category.
     *
     * @param name unique, non-blank category label
     */
    public Category(String name) {
        this.name = name;
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
     * Returns the unique category label.
     *
     * @return the unique category label
     */
    public String getName() {
        return name;
    }

    /**
     * Renames the category.
     *
     * @param name new unique, non-blank label
     */
    public void setName(String name) {
        this.name = name;
    }
}
