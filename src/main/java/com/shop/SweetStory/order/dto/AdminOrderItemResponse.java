// AdminOrderItemResponse.java
package com.shop.SweetStory.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Administrative view DTO for an order item, containing customer details,
 * selected product, order parameters, status, and price.
 *
 * @param id order identifier
 * @param delivery_date scheduled delivery date
 * @param order_status current order status
 * @param customer details of the customer who placed the order
 * @param product_name name of the ordered product
 * @param filling selected filling
 * @param decor selected decoration option
 * @param weight weight of the ordered item
 * @param order_price total order price
 */
public record AdminOrderItemResponse(
        Long id,
        LocalDate delivery_date,
        String order_status,
        OrderCustomerInfo customer,
        String product_name,
        String filling,
        String decor,
        Double weight,
        BigDecimal order_price
) {
}