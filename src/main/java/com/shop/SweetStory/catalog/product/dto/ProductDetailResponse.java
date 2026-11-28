package com.shop.SweetStory.catalog.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.SweetStory.catalog.decoration.dto.DecorationResponse;
import com.shop.SweetStory.catalog.filling.dto.FillingResponse;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        List<String> images,
        String description,
        @JsonProperty("available_fillings") List<FillingResponse> availableFillings,
        @JsonProperty("available_decors") List<DecorationResponse> availableDecors,
        Double minWeight,
        Double maxWeight,
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}