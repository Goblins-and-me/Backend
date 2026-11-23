// AdminOrderResponse.java
package com.shop.SweetStory.order.dto;

import java.util.List;

/**
 * Response DTO containing a list of orders for the administrative panel.
 *
 * @param orders list of orders in administrative format
 */
public record AdminOrderResponse(
        List<AdminOrderItemResponse> orders
) {
}