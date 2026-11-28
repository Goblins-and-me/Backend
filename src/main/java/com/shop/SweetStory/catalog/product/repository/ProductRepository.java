package com.shop.SweetStory.catalog.product.repository;

import com.shop.SweetStory.catalog.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

}
