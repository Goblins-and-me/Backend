package com.shop.SweetStory.catalog.review.repository;

import com.shop.SweetStory.catalog.review.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findAllByProductIdOrderByCreatedAtDesc(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Optional<ProductReview> findByIdAndProductId(Long reviewId, Long productId);
}
