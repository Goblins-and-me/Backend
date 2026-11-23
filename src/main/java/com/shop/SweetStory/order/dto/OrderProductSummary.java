// OrderProductSummary.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO containing summary information about a product in a user's order, with validation rules.
 *
 * @param id product identifier (required field)
 * @param name product name (required non-blank field)
 * @param image_url product image URL
 */
public record OrderProductSummary(
        @NotNull
        Long id,

        @NotBlank
        String name,

        String image_url

) {
}