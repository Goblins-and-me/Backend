// ProductSummaryResponse.java
package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO containing summary product information for display in the catalog list.
 *
 * @param id product identifier
 * @param name product name
 * @param imageUrl primary product image URL (serialized as {@code image_url} in JSON)
 * @param description product description
 * @param cakePrice product price (serialized as {@code cake_price} in JSON)
 */
public record ProductSummaryResponse(
        Long id,
        String name,
        @JsonProperty("image_url") String imageUrl,
        String description,
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}