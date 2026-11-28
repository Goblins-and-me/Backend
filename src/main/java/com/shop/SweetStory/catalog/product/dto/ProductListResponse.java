package com.shop.SweetStory.catalog.product.dto;

import java.util.List;

public record ProductListResponse (
        List<ProductSummaryResponse> items
){
}
