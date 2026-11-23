// ProductDetailResponse.java
package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO с подробной информацией о товаре каталога для клиента.
 *
 * @param id идентификатор товара
 * @param name название товара
 * @param images список изображений товара
 * @param description описание товара
 * @param availableFillings доступные начинки (в JSON {@code available_fillings})
 * @param availableDecors доступные варианты декора (в JSON {@code available_decors})
 * @param minWeight минимальный вес товара
 * @param maxWeight максимальный вес товара
 * @param cakePrice цена товара (в JSON {@code cake_price})
 */
public record ProductDetailResponse(
        Long id,
        String name,
        List<String> images,
        String description,
        @JsonProperty("available_fillings") List<String> availableFillings,
        @JsonProperty("available_decors") List<String> availableDecors,
        Double minWeight,
        Double maxWeight,
        @JsonProperty("cake_price") BigDecimal cakePrice
) {}