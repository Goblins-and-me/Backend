// UpdateProductRequest.java
package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO запроса на обновление существующего товара каталога с правилами валидации полей.
 *
 * @param name новое название товара (обязательное поле)
 * @param images обновлённый список изображений товара
 * @param description обновлённое описание товара
 * @param fillings обновлённый список начинок
 * @param decorations обновлённый список вариантов декора
 * @param minWeight новый минимальный вес товара (положительное значение)
 * @param maxWeight новый максимальный вес товара (положительное значение)
 * @param cakePrice новая цена товара (положительное значение, в JSON {@code cake_price})
 */
public record UpdateProductRequest(
        @NotBlank(message = "name must not be empty")
        String name,
        List<String> images,
        String description,
        List<String> fillings,
        List<String> decorations,
        @Positive(message = "minWeight must be positive")
        Double minWeight,
        @Positive(message = "maxWeight must be positive")
        Double maxWeight,
        @Positive(message = "cake_price must be positive")
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}