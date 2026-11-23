// UpdateOrderStatusRequest.java
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for updating an order's status with a required non-blank value rule.
 *
 * @param order_status new order status value
 */
public record UpdateOrderStatusRequest(
        @NotBlank
        String order_status
) {
}