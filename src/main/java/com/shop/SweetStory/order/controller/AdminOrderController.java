package com.shop.SweetStory.order.controller;

import com.shop.SweetStory.order.dto.UpdateOrderStatusRequest;
import com.shop.SweetStory.order.dto.UpdateOrderStatusResponse;
import com.shop.SweetStory.order.service.OrderService;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
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

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(name = "group_by", required = false) String groupBy
    ) {
        if ("date".equalsIgnoreCase(groupBy)) {
            return ResponseEntity.ok(orderService.getOrdersGroupedByDate());
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UpdateOrderStatusResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> deleteOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }
}