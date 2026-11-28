package com.shop.SweetStory.catalog.decoration.dto;

import java.math.BigDecimal;

public record CreateDecorationRequest(
        String name,

        String description,

        BigDecimal price

) {
}
