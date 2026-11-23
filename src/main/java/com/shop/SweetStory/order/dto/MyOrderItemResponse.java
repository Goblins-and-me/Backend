// MyOrderItemResponse.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for an order item in the current user's order list.
 *
 * @param id order identifier (required field)
 * @param delivery_date order delivery date (required field)
 * @param product summary information about the ordered product
 * @param filling selected filling
 * @param decor selected decoration option
 * @param weight item weight
 * @param order_price total order price
 * @param order_status current order status
 */
public record MyOrderItemResponse(

        @NotNull
        Long id,

        @NotNull
        LocalDate delivery_date,

        OrderProductSummary product,

        String filling,

        String decor,

        Double weight,

        BigDecimal order_price,

        String order_status

) {
}