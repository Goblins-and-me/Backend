package com.shop.SweetStory.catalog.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest (

        @NotNull(message = "rating must not be null")
        @Min(value = 1, message = "rating must be at least 1")
        @Max(value = 5, message = "rating must be at most 5")
        Integer rating,


        @Size(max = 2000, message = "max size is 2000")
        String comment
) {
}
