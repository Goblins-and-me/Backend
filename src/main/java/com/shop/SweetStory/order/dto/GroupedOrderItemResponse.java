// GroupedOrderItemResponse.java
package com.shop.SweetStory.order.dto;

import java.math.BigDecimal;

/**
 * DTO for an order item within a group of orders grouped by date.
 *
 * @param id order identifier
 * @param order_status current order status
 * @param customer_name customer name
 * @param customer_phone customer contact phone number
 * @param product_name name of the ordered product
 * @param filling selected filling
 * @param decor selected decoration option
 * @param weight item weight
 * @param order_price total order price
 */
public record GroupedOrderItemResponse(
        Long id,
        String order_status,
        String customer_name,
        String customer_phone,
        String product_name,
        String filling,
        String decor,
        Double weight,
        BigDecimal order_price
) {
}