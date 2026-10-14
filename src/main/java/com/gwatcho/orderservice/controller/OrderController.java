package com.gwatcho.orderservice.controller;

import com.gwatcho.orderservice.dto.CheckoutRequest;
import com.gwatcho.orderservice.dto.OrderResponse;
import com.gwatcho.orderservice.entity.OrderStatus;
import com.gwatcho.orderservice.service.CheckoutService;
import com.gwatcho.orderservice.service.OrderService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final CheckoutService checkoutService;

    // =========================================================
    // CHECKOUT
    // =========================================================
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest request) {

        OrderResponse response = checkoutService.checkout(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ORDER
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrder(id)
        );
    }

    // =========================================================
    // GET CUSTOMER ORDERS
    // =========================================================
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                orderService.getCustomerOrders(
                        customerId
                )
        );
    }

    // =========================================================
    // GET ALL ORDERS
    // =========================================================
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(
                orderService.getOrders()
        );
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }

    /**
     * Updates Order status
     * @param id The Order ID.
     * @param request The request boby
     * @return Updated Order
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusValue = request.get("status");
        if (statusValue == null || statusValue.isBlank()) {
            throw new IllegalArgumentException(
                    "Status is required"
            );
        }
        OrderStatus status = OrderStatus.valueOf(
                statusValue.trim().toUpperCase(java.util.Locale.ROOT)
        );
        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }
}