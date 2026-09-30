package com.example.week8demo.repository;

import com.example.week8demo.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for {@link Product}.
 *
 * <p><b>Topic 1 — you write zero implementations.</b> Extending
 * {@code JpaRepository&lt;Product, Long&gt;} gives roughly eighteen ready-made methods
 * ({@code save}, {@code findById}, {@code findAll}, {@code deleteById},
 * {@code existsById}, {@code count}, ...) because Spring generates a proxy class at
 * startup. The two generic arguments must match the entity ({@code Product}) and its
 * {@code @Id} field type ({@code Long}) exactly — a mismatch fails fast at startup.</p>
 *
 * <p><b>Topic 3 — derived vs custom queries.</b> Method names below the line are
 * <i>derived queries</i>: Spring parses the name into SQL. Names that would grow
 * unreadable instead use {@code @Query} JPQL with {@code @Param} bindings.</p>
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ------------------------------------------------------------------
    // Derived query methods (Topic 3): name -> WHERE clause, no JPQL needed.
    // ------------------------------------------------------------------

    /**
     * Case-insensitive keyword search on {@code name}.
     * {@code Containing} wraps the parameter in {@code %...%} automatically, and
     * {@code IgnoreCase} lower-cases both sides.
     *
     * @param keyword substring to look for, e.g. {@code "pen"}
     * @return matching products, possibly empty, never {@code null}
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * All products cheaper than the given ceiling.
     *
     * @param maxPrice exclusive upper bound
     * @return products with {@code price < maxPrice}
     */
    List<Product> findByPriceLessThan(Double maxPrice);

    /**
     * Active products in a category, cheapest first.
     * Shows how {@code And} combines predicates and {@code OrderBy} adds sorting.
     *
     * @param category exact category label
     * @return active matches ordered by ascending price
     */
    List<Product> findByCategoryAndActiveTrueOrderByPriceAsc(String category);

    /**
     * Cheap existence check: issues a lean {@code SELECT 1 ...} instead of loading
     * the row. Prefer this over {@code findById(...).isPresent()} when you only
     * need a yes/no answer (for example, guarding a delete).
     *
     * @param name product name to test
     * @return {@code true} when a product with that name exists
     */
    boolean existsByName(String name);

    /**
     * Counts active products. The trailing {@code True} is part of the method name,
     * not a parameter — Spring turns it into {@code WHERE active = true}.
     *
     * @return number of active products
     */
    long countByActiveTrue();

    /**
     * Finds one product by its unique name.
     *
     * @param name exact product name
     * @return the match, or {@link Optional#empty()} when absent
     */
    Optional<Product> findByName(String name);

    // ------------------------------------------------------------------
    // Custom JPQL queries (Topic 3): use when the name would get too long,
    // when OR/AND precedence gets ambiguous, or when you need real JPQL power.
    // NOTE: JPQL uses entity/field names (Product, category, price, active) —
    // never table/column names.
    // ------------------------------------------------------------------

    /**
     * Case-insensitive category lookup written as JPQL. Same result as a derived
     * method, but the intent is visible in the annotation instead of a long name.
     *
     * @param category category label in any letter case
     * @return products whose category matches, ignoring case
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findByCategoryIgnoreCase(@Param("category") String category);

    /**
     * Active products inside an inclusive price band, cheapest first.
     * A three-condition query like this is the textbook case for {@code @Query}:
     * the derived-name equivalent would be unreadable.
     *
     * @param min lowest price, inclusive
     * @param max highest price, inclusive
     * @return active products with {@code min <= price <= max}
     */
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findActiveInPriceRange(@Param("min") Double min, @Param("max") Double max);

    /**
     * Bulk price update without loading entities first.
     *
     * <p><b>Two annotations are mandatory for writes via {@code @Query}:</b>
     * {@code @Modifying} (this is not a SELECT) plus {@code @Transactional} on the
     * calling service method (writes need a transaction). Returns the number of
     * rows affected.</p>
     *
     * @param category only products in this category are touched
     * @param price    the new price applied to every match
     * @return how many rows were updated
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.price = :price WHERE p.category = :category")
    int bulkUpdatePriceByCategory(@Param("category") String category, @Param("price") Double price);
}
