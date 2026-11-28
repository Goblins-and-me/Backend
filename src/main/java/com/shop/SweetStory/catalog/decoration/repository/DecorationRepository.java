package com.shop.SweetStory.catalog.decoration.repository;

import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecorationRepository extends JpaRepository<Decoration, Long> {
    Optional<Product> findByName(String name);
}
