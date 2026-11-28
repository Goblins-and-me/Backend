package com.shop.SweetStory.catalog.review.controller;

import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.catalog.review.dto.CreateReviewRequest;
import com.shop.SweetStory.catalog.review.dto.ReviewListResponse;
import com.shop.SweetStory.catalog.review.dto.ReviewResponse;
import com.shop.SweetStory.catalog.review.dto.UpdateReviewRequest;
import com.shop.SweetStory.catalog.review.service.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ReviewListResponse> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        ReviewResponse created = reviewService.createReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(productId, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ActionMessageResponse> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.deleteReview(productId, reviewId));
    }
}