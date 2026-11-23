// CreateProductResponse.java
package com.shop.SweetStory.catalog.dto;

/**
 * DTO ответа при успешном создании нового товара в каталоге.
 *
 * @param status статус выполнения операции
 * @param product подробные данные созданного товара
 */
public record CreateProductResponse (
        String status,
        ProductDetailResponse product
) {
}