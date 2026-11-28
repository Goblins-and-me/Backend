package com.shop.SweetStory.catalog.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long productId,
        String authorName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
