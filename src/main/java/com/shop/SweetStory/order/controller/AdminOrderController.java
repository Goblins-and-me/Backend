package com.shop.SweetStory.order.controller;

import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.order.dto.AdminOrderResponse;
import com.shop.SweetStory.order.dto.GroupedOrdersResponse;
import com.shop.SweetStory.order.dto.UpdateOrderStatusRequest;
import com.shop.SweetStory.order.dto.UpdateOrderStatusResponse;
import com.shop.SweetStory.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for administrative order management mapped to {@code /api/admin/orders}.
 * Allows retrieving orders (including grouped by date), updating order status,
 * and deleting orders, delegating business logic to {@link OrderService}.
 */
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    // Service for managing orders
    private final OrderService orderService;

    /**
     * Creates an administrative order controller with the injected order service.
     *
     * @param orderService order management service
     */
    public AdminOrderController(OrderService orderService) {
        // Store reference to the order service
        this.orderService = orderService;
    }

    /**
     * Returns all orders or orders grouped by date, depending on the {@code group_by} query parameter.
     *
     * @param groupBy optional grouping parameter ({@code "date"} to group orders by date)
     * @return {@code 200 OK} response with {@link GroupedOrdersResponse} when grouped by date,
     *         or with {@link AdminOrderResponse} / order list in standard mode
     */
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(name = "group_by", required = false) String groupBy
    ) {
        // Return date-grouped orders when group_by=date is specified (case-insensitive)
        if ("date".equalsIgnoreCase(groupBy)) {
            return ResponseEntity.ok(orderService.getOrdersGroupedByDate());
        }
        // Otherwise return the full list of orders
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Updates the status of an existing order by its identifier.
     *
     * @param id order identifier from the request path
     * @param request validated order status update payload
     * @return {@code 200 OK} response with the updated order status details
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UpdateOrderStatusResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        // Pass order ID and status update request to the service and return HTTP 200 OK
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    /**
     * Deletes an order by its identifier.
     *
     * @param id order identifier from the request path
     * @return {@code 200 OK} response with a deletion confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> deleteOrder(@PathVariable Long id) {
        // Delete the order by ID via the service and return HTTP 200 OK with the operation result
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }
}