// OrderCreatedItemResponse.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO containing created order details returned after placing an order.
 *
 * @param id created order identifier (required field)
 * @param delivery_date order delivery date (required field)
 * @param product_name name of the ordered product (required non-blank field)
 * @param filling selected filling
 * @param decor selected decoration option
 * @param weight item weight
 * @param order_price total order price
 * @param order_status initial order status
 */
public record OrderCreatedItemResponse(
        @NotNull
        Long id,

        @NotNull
        LocalDate delivery_date,

        @NotBlank
        String product_name,

        String filling,

        String decor,

        Double weight,

        BigDecimal order_price,

        String order_status

) {
}