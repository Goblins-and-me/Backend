package com.shop.SweetStory.catalog.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
        @Min(value = 1, message = "rating must be at least 1")
        @Max(value = 5, message = "rating must be at most 5")
        Integer rating,

        @Size(max = 2000, message = "comment is too long")
        String comment
) {
}
