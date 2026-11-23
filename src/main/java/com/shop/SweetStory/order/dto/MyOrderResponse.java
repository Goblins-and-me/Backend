// MyOrderResponse.java
package com.shop.SweetStory.order.dto;

import java.util.List;

/**
 * Response DTO containing the list of orders for the current user.
 *
 * @param orders list of the user's orders
 */
public record MyOrderResponse(
        List<MyOrderItemResponse> orders
) {
}