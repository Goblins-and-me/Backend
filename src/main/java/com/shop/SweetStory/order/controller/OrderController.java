package com.shop.SweetStory.order.controller;

import com.shop.SweetStory.order.dto.CreateOrderRequest;
import com.shop.SweetStory.order.dto.CreateOrderResponse;
import com.shop.SweetStory.order.dto.MyOrderResponse;
import com.shop.SweetStory.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user orders mapped to {@code /api/orders}.
 * Handles creating new orders and retrieving the current user's orders,
 * delegating business logic to {@link OrderService}.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Service for processing and retrieving orders
    private final OrderService orderService;

    /**
     * Creates an order controller with the injected order service.
     *
     * @param orderService order management service
     */
    public OrderController(OrderService orderService) {
        // Store reference to the order service
        this.orderService = orderService;
    }

    /**
     * Creates a new order from validated request data.
     *
     * @param request order details validated via Jakarta Bean Validation
     * @return {@code 201 Created} response with the created order details
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        // Pass the request to the service to create the order and retrieve the result
        CreateOrderResponse created = orderService.createOrder(request);
        // Return HTTP 201 Created with the created order payload
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Returns the list of orders belonging to the currently authenticated user.
     *
     * @return {@code 200 OK} response containing the user's orders
     */
    @GetMapping("/my")
    public ResponseEntity<MyOrderResponse> getMyOrders() {
        // Request the current user's orders from the service and return HTTP 200 OK
        return ResponseEntity.ok(orderService.getMyOrders());
    }
}