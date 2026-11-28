package com.shop.SweetStory.catalog.filling.dto;

import java.math.BigDecimal;

public record CreateFillingRequest(
        String name,

        String description,

        BigDecimal price
) {
}
