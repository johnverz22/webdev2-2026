package com.example.week8demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the WEBDEV2 Week 8 demo application.
 *
 * <p><b>What this demo covers (Week 8 — Spring Data JPA and CRUD):</b></p>
 * <ul>
 *   <li><b>Topic 1 — Repository interfaces:</b> see
 *       {@link com.example.week8demo.repository.ProductRepository} and
 *       {@link com.example.week8demo.repository.CategoryRepository}. Each interface
 *       extends {@code JpaRepository} and Spring generates the implementation at startup.</li>
 *   <li><b>Topic 2 — CRUD:</b> see
 *       {@link com.example.week8demo.service.ProductService} and
 *       {@link com.example.week8demo.controller.ProductController}.</li>
 *   <li><b>Topic 3 — Derived and custom queries:</b> query methods declared on
 *       {@link com.example.week8demo.repository.ProductRepository}.</li>
 *   <li><b>Topic 4 — Transactions:</b> {@code @Transactional} methods in
 *       {@link com.example.week8demo.service.ProductService}.</li>
 *   <li><b>Topic 5 — Debugging:</b> {@code application.properties} (MySQL wiring,
 *       SQL logging) plus {@link com.example.week8demo.exception.GlobalExceptionHandler}.</li>
 * </ul>
 *
 * <p>Run with {@code mvnw.cmd spring-boot:run}, then open
 * {@code http://localhost:8080/products} in a browser. The demo uses your locally
 * hosted MySQL server (database {@code webdev2_week8}) — verify rows with the
 * MySQL command-line client from INFOMAN1, e.g.
 * {@code SELECT * FROM products;}.</p>
 */
@SpringBootApplication
public class Week8DemoApplication {

    /**
     * Boots the Spring application context (component scan, JPA repositories,
     * transaction proxies) and starts the embedded web server.
     *
     * @param args command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(Week8DemoApplication.class, args);
    }
}
