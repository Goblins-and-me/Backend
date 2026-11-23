package com.shop.SweetStory.catalog.repository;

import com.shop.SweetStory.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for working with {@link Product} entities
 * and executing queries against the {@code products} table.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a product by its exact name.
     *
     * @param name product name
     * @return {@link Optional} containing the matching product, or an empty {@link Optional} if none is found
     */
    Optional<Product> findByName(String name);

}