// CreateOrderResponse.java
package com.shop.SweetStory.order.dto;

/**
 * Response DTO returned when an order is created successfully.
 *
 * @param status operation status
 * @param message descriptive message about the order creation result
 * @param order created order details
 */
public record CreateOrderResponse (
        String status,

        String message,

        OrderCreatedItemResponse order

) {

    /**
     * Creates a response for a successfully created order with status {@code "success"}
     * and default message {@code "order successfully created"}.
     *
     * @param order created order details
     */
    public CreateOrderResponse(OrderCreatedItemResponse order){
        this("success", "order successfully created", order);
    }

}