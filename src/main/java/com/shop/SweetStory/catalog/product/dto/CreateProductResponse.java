package com.shop.SweetStory.catalog.product.dto;

public record CreateProductResponse (
        String status,
        ProductDetailResponse product
) {
}
