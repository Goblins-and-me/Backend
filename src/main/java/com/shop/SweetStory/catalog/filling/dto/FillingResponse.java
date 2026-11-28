package com.shop.SweetStory.catalog.filling.dto;

import java.math.BigDecimal;

public record FillingResponse(
        Long id,

        String name,

        String description,

        BigDecimal price
) {
}
