package com.example.week8demo.service;

import com.example.week8demo.entity.Product;
import com.example.week8demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business layer for products. The controller never touches the repository
 * directly — every database operation goes through here so the transaction
 * boundary (Topic 4) wraps the whole business operation, not single queries.
 *
 * <p><b>Topic 2 — CRUD and {@code Optional}:</b> {@code findById} returns
 * {@code Optional&lt;Product&gt;}, never {@code null}. Absence-as-error uses
 * {@code orElseThrow}; absence-as-normal would use {@code orElse} or
 * {@code ifPresent}. Never call {@code .get()} blind.</p>
 *
 * <p><b>Topic 4 — transactions:</b> write methods run inside
 * {@code @Transactional} (commit everything or roll everything back); pure reads
 * use {@code @Transactional(readOnly = true)} (clearer intent, faster because
 * Hibernate skips dirty checking). Rollback happens automatically for
 * <i>unchecked</i> exceptions ({@code RuntimeException}); checked exceptions commit
 * unless you add {@code rollbackFor}.</p>
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor injection — the test-friendly way to receive the repository
     * proxy Spring generated for {@code ProductRepository}.
     *
     * @param productRepository the Spring Data proxy, injected by the container
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ------------------------------------------------------------------
    // Reads (Topic 2 + Topic 4 readOnly)
    // ------------------------------------------------------------------

    /**
     * Lists every product. Fine for a demo table; on a huge table you would
     * paginate instead of loading everything into memory.
     *
     * @return all products, empty list when the table is empty, never {@code null}
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Fetches one product or fails loudly with the id in the message.
     *
     * @param id primary key to look up
     * @return the matching product
     * @throws EntityNotFoundException when no row has that id (mapped to HTTP 404
     *                                 by {@code GlobalExceptionHandler})
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    /**
     * Keyword search demo for Topic 3 (derived {@code ContainingIgnoreCase}).
     *
     * @param keyword substring to match inside the name
     * @return matches, possibly empty
     */
    @Transactional(readOnly = true)
    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Price-ceiling demo for Topic 3 (derived {@code LessThan}).
     *
     * @param maxPrice exclusive upper bound
     * @return products cheaper than {@code maxPrice}
     */
    @Transactional(readOnly = true)
    public List<Product> cheaperThan(Double maxPrice) {
        return productRepository.findByPriceLessThan(maxPrice);
    }

    /**
     * Category lookup demo for Topic 3 (custom JPQL + {@code @Param}).
     *
     * @param category category label in any letter case
     * @return matches, ignoring case
     */
    @Transactional(readOnly = true)
    public List<Product> byCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    // ------------------------------------------------------------------
    // Writes (Topic 2 CRUD + Topic 4 transactions)
    // ------------------------------------------------------------------

    /**
     * Creates a product. The entity's {@code id} must be {@code null} so
     * {@code save()} takes the INSERT path; a preset id would take the UPDATE
     * (merge) path and silently do nothing when the row does not exist.
     *
     * @param product new product with {@code null} id
     * @return the saved product, now carrying its generated id
     */
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Updates name/price/category/stock/active of an existing product.
     *
     * @param id      id of the product to change
     * @param updated replacement field values (its own id, if any, is ignored)
     * @return the updated product
     * @throws EntityNotFoundException when {@code id} does not exist
     */
    @Transactional
    public Product updateProduct(Long id, Product updated) {
        Product existing = getProductById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setStock(updated.getStock());
        existing.setActive(updated.isActive());
        return productRepository.save(existing);
    }

    /**
     * Deletes a product. Checks existence first so a missing id yields a clean
     * 404 instead of the raw {@code EmptyResultDataAccessException} that
     * {@code deleteById} throws on a miss.
     *
     * @param id id of the product to remove
     * @throws EntityNotFoundException when {@code id} does not exist
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Saves every product in one transaction — <b>the Topic 4 atomicity demo</b>.
     *
     * <p>Try it: POST two products where the second reuses an existing
     * {@code name}. The second INSERT violates the UNIQUE constraint, Spring throws
     * {@code DataIntegrityViolationException} (unchecked, so rollback is automatic),
     * and the first INSERT is rolled back too. Query afterwards: neither row exists.
     * Watch the console with {@code show-sql} on — you will see the first INSERT
     * printed and then undone.</p>
     *
     * @param products batch to persist atomically
     * @throws org.springframework.dao.DataIntegrityViolationException when any row
     *         violates a constraint; nothing in the batch is committed
     */
    @Transactional
    public void batchCreate(List<Product> products) {
        for (Product p : products) {
            productRepository.save(p);
        }
    }

    /**
     * Sells units of a product: decrement stock and record nothing else, in one
     * transaction. Fails with {@code IllegalStateException} (unchecked, rolls back)
     * when stock would go negative, so stock is never left half-updated.
     *
     * @param id  product to sell from
     * @param qty units to remove, must be positive
     * @return the product with its new stock level
     * @throws EntityNotFoundException when {@code id} does not exist
     * @throws IllegalArgumentException when {@code qty} is not positive
     * @throws IllegalStateException    when stock is insufficient (rolls back)
     */
    @Transactional
    public Product sell(Long id, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Product product = getProductById(id);
        if (product.getStock() < qty) {
            throw new IllegalStateException("Insufficient stock for product " + id);
        }
        product.setStock(product.getStock() - qty);
        return productRepository.save(product);
    }
}
