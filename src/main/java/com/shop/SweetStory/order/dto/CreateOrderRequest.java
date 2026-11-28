package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateOrderRequest(
        @NotNull
        LocalDate delivery_date,

        @NotNull
        Long product_id,

        Long filling_id,

        Long decoration_id,

        @NotNull
        @Positive
        Double weight
) {
}