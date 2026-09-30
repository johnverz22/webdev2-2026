package com.example.week8demo.repository;

import com.example.week8demo.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Category}.
 *
 * <p><b>Topic 1 exercise:</b> this second repository proves the "one interface, zero
 * implementation" rule generalises — Spring detects <i>every</i> interface extending
 * {@code JpaRepository} under the application package and builds a proxy for each.
 * Delete this file and the app still boots; add a third repository and it still
 * boots, as long as the generic types match the entity.</p>
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Looks up a category by its unique label.
     *
     * @param name exact category name
     * @return the match, or {@link Optional#empty()} when absent
     */
    Optional<Category> findByName(String name);
}
