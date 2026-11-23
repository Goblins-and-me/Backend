// CreateProductRequest.java
package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO запроса на создание нового товара в каталоге с правилами валидации полей.
 *
 * @param name название товара (обязательное поле)
 * @param images список ссылок или путей к изображениям товара
 * @param description описание товара
 * @param fillings доступные варианты начинок
 * @param decorations доступные варианты декора
 * @param minWeight минимальный вес товара (обязательное положительное значение)
 * @param maxWeight максимальный вес товара (обязательное положительное значение)
 * @param cakePrice базовая цена товара (обязательное положительное значение, в JSON {@code cake_price})
 */
public record CreateProductRequest(

        @NotBlank(message = "name must not be empty")
        String name,
        List<String> images,
        String description,
        @JsonProperty("fillings") List<String> fillings,
        @JsonProperty("decorations") List<String> decorations,
        @NotNull(message = "minWeight is required")
        @Positive(message = "minWeight must be positive")
        Double minWeight,
        @NotNull(message = "maxWeight is required")
        @Positive(message = "maxWeight must be positive")
        Double maxWeight,
        @NotNull(message = "cake_price is required")
        @Positive(message = "cake_price must be positive")
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}