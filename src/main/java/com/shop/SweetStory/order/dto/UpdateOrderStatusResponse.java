// UpdateOrderStatusResponse.java
package com.shop.SweetStory.order.dto;

/**
 * Response DTO returned after updating an order's status.
 *
 * @param status operation status
 * @param order_id identifier of the updated order
 * @param new_status current order status after the update
 */
public record UpdateOrderStatusResponse(
        String status,
        Long order_id,
        String new_status
) {}