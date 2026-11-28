package com.shop.SweetStory.catalog.decoration.dto;

import java.math.BigDecimal;

public record DecorationResponse (

        Long id,

        String name,

        String description,

        BigDecimal price

) {
}
