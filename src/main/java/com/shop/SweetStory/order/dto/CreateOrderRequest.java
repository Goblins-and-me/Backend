// CreateOrderRequest.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Request DTO for creating a new order with validation rules for required fields and weight.
 *
 * @param delivery_date requested delivery date (required field)
 * @param product_id identifier of the selected product (required field)
 * @param filling selected filling
 * @param decor selected decoration option
 * @param weight item weight (required positive value)
 */
public record CreateOrderRequest(

        @NotNull
        LocalDate delivery_date,

        @NotNull
        Long product_id,

        String filling,

        String decor,

        @NotNull
        @Positive
        Double weight
) {
}